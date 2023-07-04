package com.mlorenzo.photoapp.api.gateway.security;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;


@EnableWebSecurity // Esta anotación ya incluye la anotación @Configuration y, por lo tanto, no hace falta ponerla aquí
public class WebSecurity extends WebSecurityConfigurerAdapter {
	private final Environment env;
	
	public WebSecurity(Environment env) {
		this.env = env;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers(HttpMethod.POST, env.getProperty("api.registration.url.path"), env.getProperty("api.login.url.path")).permitAll()
			.antMatchers(env.getProperty("api.h2console.url.path")).permitAll()
			.antMatchers(env.getProperty("api.zuul.actuator")).permitAll()
			.antMatchers(env.getProperty("api.users.actuator.url.path")).permitAll()
			.anyRequest().authenticated()
			.and()
			.addFilter(new AuthorizationFilter(authenticationManager(), env))
			// Para cambiar el comportamiento por defecto de Spring Security que devuele el estado de error 403 cuando el usuario no está autenticado en lugar del estado de error 401
			.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
			.and()
			.csrf().disable()
			// Se desactiva el manejo de sesión de Spring Security(por defecto activado) porque la autenticación basada en JWT(Jason Web Token) es sin estado(stateless)
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			// Para poder visualizar la consola de la base de datos embebida H2 correctamente
			.headers().frameOptions().disable();
	}
}
