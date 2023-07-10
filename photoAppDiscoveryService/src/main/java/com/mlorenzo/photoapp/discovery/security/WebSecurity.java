package com.mlorenzo.photoapp.discovery.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Activamos la seguridad de Spring Security cuando hay conexión con el servidor de configuraciones y se obtiene la propiedad "security.custom.enabled" con valor true
@ConditionalOnProperty(prefix = "security.custom", name = "enabled", havingValue="true", matchIfMissing = false)
@Configuration
@EnableWebSecurity
public class WebSecurity {
	
	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests()
			.anyRequest().authenticated()
			.and()
			// Tenemos que desactivar la protección CSRF para que los microservicios puedan registrarse en este servidor Eureka correctamente
			.csrf().disable()
			.httpBasic();
		return http.build();
	}
}
