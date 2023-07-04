package com.mlorenzo.photoapp.api.users;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.ForwardedHeaderFilter;

import feign.Logger;


@SpringBootApplication
// Opcional. En las últimas versiones de Spring Cloud, ya no hace falta añadir esta anotación para el registro de una aplicación, o microservicio, en un servidor de descubrimiento Eureka
// Es decir, si en el classpath de la aplicación se localiza la dependencia, o librería, "spring-cloud-starter-netflix-eureka-client", automáticamente se procede al registro de la aplicación en el servidor Eureka
//@EnableDiscoveryClient
@EnableFeignClients
@EnableCircuitBreaker
public class PhotoAppApiUsersApplication {
	
	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(PhotoAppApiUsersApplication.class, args);
	}
	
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper;
	}
	
	@Bean
	public PasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	// Bean de Spring necesario para habilitar o deshabilitar(opción por defecto si no se crea un bean de Spring con otra configuración)los logs del cliente Feign
	// Sólo para depurar en el entorno de Desarrollo
	@Bean
	@Profile("!production")
	public Logger.Level feignLoggerLevel() {
		// FULL - Log the headers, body, and metadata for both requests and responses
		return Logger.Level.FULL;
	}
	
	// Bean de Spring necesario para habilitar o deshabilitar(opción por defecto si no se crea un bean de Spring con otra configuración)los logs del cliente Feign
	// Por temas de seguridad, se desactiva el logging del cliente Feign en el entorno de Producción
	@Bean
	@Profile("production")
	public Logger.Level feignDefaultLoggerLevel() {
		// NONE - No logging
		return Logger.Level.NONE;
	}

	@Bean
	@Profile("production")
	public String createProductionBean() {
		System.out.println("Production bean created, myapplication.environment = " + env.getProperty("myapplication.environment"));
		return "Production bean";
	}
	
	@Bean
	@Profile("!production")
	public String createNotProductionBean() {
		System.out.println("Not Production bean created, myapplication.environment = " + env.getProperty("myapplication.environment"));
		return "Not Production bean";
	}
	
	@Bean
	@Profile("default")
	public String createDevelopmentBean() {
		System.out.println("Development bean created, myapplication.environment = " + env.getProperty("myapplication.environment"));
		return "Development bean";
	}
	
	// Por defecto, las cabeceras de tipo proxy("X-Forwarded-*"), que son establecidas por defecto en el proxy inverso Zuul, son ignoradas.
	// En nuestro caso, creamos este bean para que dichas cabeceras se tengan en cuenta de cara a la construcción de la url de la redirección
	// que se produce para acceder a la consola H2(se produce debido a la activación de la propiedad "spring.h2.console.settings.web-allow-others"
	// para permitir conexiones remotas). Es decir, sin esas cabeceras, la url de la redirección utiliza el hostname y el puerto donde se ejecuta
	// este microservicio y, cuando este microservicio se despliegue en Docker, esta url estará formada por el hostname dentro de Docker que sólamente
	// puede ser resuelto dentro de Docker pero no desde el navegador que esta fuera de Docker. Por esta razón, usamos estas cabeceras para que la url
	// de la redirección se construya usando siempre el hostname y el puerto del proxy inverso Zuul y, de esta forma, todas las urls correspondientes
	// al acceso a la consola H2 pasarán por este proxy inverso Zuul.
	@Bean
	public ForwardedHeaderFilter forwardedHeaderFilter() {
		return new ForwardedHeaderFilter();
	}
}
