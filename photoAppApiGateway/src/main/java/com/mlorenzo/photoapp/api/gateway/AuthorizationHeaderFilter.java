package com.mlorenzo.photoapp.api.gateway;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
	private Environment env;
	
	public AuthorizationHeaderFilter(Environment env) {
		super(Config.class);
		this.env = env;
	}
	
	public static class Config {
		// Put configuration properties here
	}

	// GatewayFilter es una interfaz funcional y, por lo tanto, podemos implementarla usando programación funcional con lambdas
	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			String authorizationHeaderName = env.getProperty("authorization.token.header.name");
			String authorizationHeaderPrefix = env.getProperty("authorization.token.header.prefix") + " ";
			ServerHttpRequest request = exchange.getRequest();
			if(!request.getHeaders().containsKey(authorizationHeaderName))
				return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
			String authorizationHeaderValue = request.getHeaders().get(authorizationHeaderName).get(0);
			if(!authorizationHeaderValue.startsWith(authorizationHeaderPrefix))
				return onError(exchange, "No authorization prefix", HttpStatus.UNAUTHORIZED);
			String jwtToken = authorizationHeaderValue.replace(authorizationHeaderPrefix, "");
			if(!isJwtValid(jwtToken)) 
				return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
			return chain.filter(exchange);
		};
	}
	
	private Mono<Void> onError(ServerWebExchange exhange, String err, HttpStatus httpStatus) {
		ServerHttpResponse response = exhange.getResponse();
		response.setStatusCode(httpStatus);
		return response.setComplete();
	}
	
	private boolean isJwtValid(String jwtToken) {
		SecretKey secretKey = new SecretKeySpec(Base64.getEncoder().encode(env.getProperty("token.secret").getBytes()), SignatureAlgorithm.HS512.getJcaName());
		JwtParser jwtParser = Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build();
		String subject = null;
		try {
			Claims claims = jwtParser.parseClaimsJws(jwtToken).getBody();
			subject = claims.getSubject();
		}
		catch(Exception e) {
			return false;
		}
		if(subject == null || subject.isEmpty())
			return false;
		return true;
	}
}
