package com.mlorenzo.photoapp.api.users.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

// Nota: Aunque el token JWT se valida en Zuul, también se valida en este microservicio para poder implementar el control de acceso a nivel de método del controlador

public class AuthorizationFilter extends BasicAuthenticationFilter {
	private Environment environment;
	
	public AuthorizationFilter(AuthenticationManager authenticationManager, Environment environment) {
		super(authenticationManager);
		this.environment = environment;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String authorizationHeaderValue = request.getHeader(environment.getProperty("authorization.token.header.name"));
		String authorizationHeaderPrefix = environment.getProperty("authorization.token.header.prefix") + " ";
		if(authorizationHeaderValue == null || !authorizationHeaderValue.startsWith(authorizationHeaderPrefix)) {		
			chain.doFilter(request, response);
			return;
		}
		String token = authorizationHeaderValue.replace(authorizationHeaderPrefix, "");
		UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(String token) {
		String userId = Jwts.parser()
				.setSigningKey(Base64.getEncoder().encode(environment.getProperty("token.secret").getBytes()))
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		if(userId == null || userId.isEmpty())
			return null;
		return new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
	}
}
