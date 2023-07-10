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
}
