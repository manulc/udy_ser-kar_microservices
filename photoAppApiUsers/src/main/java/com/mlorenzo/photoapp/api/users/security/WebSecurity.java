package com.mlorenzo.photoapp.api.users.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import com.mlorenzo.photoapp.api.users.services.UserService;

// Habilitamos el uso de las anotaciones de seguridad @PreAuthorize y @PostAuthorize
// La anotación @PreAuthorize recibe una expresión que se evalua antes de que se ejecute el código del método donde se aplica y, si se evalua a true, entonces se ejecuta ese código del método
// La anotación @PostAuthorize recibe una expresión que se evalua después de ejecutarse el código del método donde se aplica pero antes de devolverse el resultado de ese método, es decir, si la expresión se evalua a true, entonces se devuelve el resultado del método
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class WebSecurity {
	private UserService userService;
	private Environment environment;
	private PasswordEncoder passwordEncoder;
	
	public WebSecurity(UserService userService, Environment environment, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.environment = environment;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);
		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, environment, authenticationManager);
		// Por defecto, Spring Security proporcina la ruta "/login" para realizar el proceso de autenticación de un usuario
		// Usamos el valor de nuestra propiedad "login.url.path" para establecer nuestra propia ruta de autenticación
		authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		http
			.csrf().disable()
			.authorizeHttpRequests()
				//.requestMatchers(HttpMethod.POST, "/users").permitAll()
				//.requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
				.requestMatchers("/**").access(new WebExpressionAuthorizationManager(String.format("hasIpAddress('%s')", environment.getProperty("gateway.ip"))))
			.and()
			.authenticationManager(authenticationManager)
			// Añadimos nuestros filtros después del filtro AuthorizationFilter de Spring Security(no el nuestro!) para que se ejecuten después de comprobar los "RequestMatchers", es decir, en nuestro caso, queremos
			// verificar primero que todas las peticiones http vienen desde proxy inverso API Gateway y, después, aplicar nuestro filtro de autentincación o de autorización. Si usamos el método
			// "addFilter" para registrar nuestros filtros, esos filtros se ejecutarán antes que la comprobación de los "RequestMatchers" que tiene lugar en el filtro AuthorizationFilter de Spring Security.
			.addFilterAfter(authenticationFilter, org.springframework.security.web.access.intercept.AuthorizationFilter.class)
			.addFilterAfter(new AuthorizationFilter(authenticationManager, environment), org.springframework.security.web.access.intercept.AuthorizationFilter.class)
			// Para cambiar el comportamiento por defecto de Spring Security que devuele el estado de error 403 cuando el usuario no está autenticado en lugar del estado de error 401
			.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
			.and()
			// Se desactiva el manejo de sesión de Spring Security(por defecto activado) porque la autenticación basada en JWT(Jason Web Token) es sin estado(stateless)
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			// Para poder visualizar la consola de la base de datos embebida H2 correctamente, es decir, la consola H2 se carga mediante iFrames y, por defecto, Spring
			// Security no permite el uso de iFrames
			.headers().frameOptions().disable();
		return http.build();
	}
}
