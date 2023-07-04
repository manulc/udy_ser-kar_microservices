package com.mlorenzo.photoapp.discovery.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// Activamos la seguridad de Spring Security cuando hay conexión con el servidor de configuraciones y se obtiene la propiedad "security.custom.enabled" con valor true
@ConditionalOnProperty(prefix = "security.custom", name = "enabled", havingValue="true", matchIfMissing = false)
@EnableWebSecurity // Esta anotación ya incluye la anotación @Configuration y, por lo tanto, no hace falta ponerla aquí
public class WebSecurity extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.anyRequest().authenticated()
			.and()
			// Tenemos que desactivar la protección CSRF para que los microservicios puedan registrarse en este servidor Eureka correctamente
			.csrf().disable()
			.httpBasic();
	}
}
