package com.mlorenzo.photoapp.api.users.security;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlorenzo.photoapp.api.users.services.UserService;
import com.mlorenzo.photoapp.api.users.ui.models.requests.LoginRequestModel;
import com.mlorenzo.photoapp.api.users.ui.models.responses.UserResponseModel;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private UserService userService;
	private Environment environment;
	
	public AuthenticationFilter(UserService userService, Environment environment, AuthenticationManager authenticationManager) {
		super.setAuthenticationManager(authenticationManager);
		this.userService = userService;
		this.environment = environment;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			LoginRequestModel creds = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);
			// Aquí, detrás del escenario, se invoca al método loadUserByUsername de la interfaz UserDetailsService de Spring Security cuya implementación se encuentra en la clase UserServiceImpl
			// Esta implementación va a la base de datos para comprobar si hay algún usuario registrado con el email y el password que se obtienen en este método
			return getAuthenticationManager()
					.authenticate(new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword()));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {		
		String username = ((User)authResult.getPrincipal()).getUsername(); // username = email
		// Tenemos que volver a la base de datos porque se necesita el userId para el subject del token JWT
		UserResponseModel userDetails = userService.getUserDetailsByEmail(username);
		Instant now = Instant.now();
		SecretKey secretKey = new SecretKeySpec(Base64.getEncoder().encode(environment.getProperty("token.secret").getBytes()), SignatureAlgorithm.HS512.getJcaName());
		String jwtToken = Jwts.builder()
				.setSubject(userDetails.getUserId())
				.setExpiration(Date.from(now.plusMillis(Long.parseLong(environment.getProperty("token.expiration_time")))))
				.setIssuedAt(Date.from(now))
				.signWith(secretKey)
				.compact();
		response.addHeader("token", jwtToken);
		response.addHeader("userId", userDetails.getUserId());
	}
}
