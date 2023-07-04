package com.mlorenzo.photoapp.api.users.security;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import com.mlorenzo.photoapp.api.users.services.UserService;

// Habilitamos el uso de las anotaciones de seguridad @PreAuthorize y @PostAuthorize
// La anotación @PreAuthorize recibe una expresión que se evalua antes de que se ejecute el código del método donde se aplica y, si se evalua a true, entonces se ejecuta ese código del método
// La anotación @PostAuthorize recibe una expresión que se evalua después de ejecutarse el código del método donde se aplica pero antes de devolverse el resultado de ese método, es decir, si la expresión se evalua a true, entonces se devuelve el resultado del método
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity // Esta anotación ya incluye la anotación @Configuration y, por lo tanto, no hace falta ponerla aquí
public class WebSecurity extends WebSecurityConfigurerAdapter {
	private UserService userService;
	private Environment environment;
	private PasswordEncoder passwordEncoder;
	
	public WebSecurity(UserService userService, Environment environment, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.environment = environment;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeRequests().antMatchers("/**").hasIpAddress(environment.getProperty("gateway.ip"))
			.and()
			// Añadimos nuestros filtros después del filtro FilterSecurityInterceptor para que se ejecuten después de comprobar los "AntMatchers", es decir, en nuestro caso, queremos
			// verificar primero que todas las peticiones http vienen desde proxy inverso Zuul y, después, aplicar el filtro de autentincación o de autorización. Si usamos el método
			// "addFilter" para registrar nuestros filtros, esos filtros se ejecutarán antes que la comprobación de los "AntMatcher" que tiene lugar en el filtro FilterSecurityInterceptor.
			.addFilterAfter(getAuthenticationFilter(), FilterSecurityInterceptor.class)
			.addFilterAfter(new AuthorizationFilter(authenticationManager(), environment), FilterSecurityInterceptor.class)
			// Para cambiar el comportamiento por defecto de Spring Security que devuele el estado de error 403 cuando el usuario no está autenticado en lugar del estado de error 401
			.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
			.and()
			// Se desactiva el manejo de sesión de Spring Security(por defecto activado) porque la autenticación basada en JWT(Jason Web Token) es sin estado(stateless)
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			//.and()
			// Para poder visualizar la consola de la base de datos embebida H2 correctamente
			//.headers().frameOptions().disable();
	}

	private AuthenticationFilter getAuthenticationFilter() throws Exception {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, environment, authenticationManager());
		// Por defecto, Spring Security proporcina la ruta "/login" para realizar el proceso de login de un usuario
		// Usamos el valor de nuestra propiedad "login.url.path" para establecer nuestra propia ruta de login
		authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		return authenticationFilter;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
	}
}
