package com.mlorenzo.photoapp.api.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import reactor.core.publisher.Mono;

// Definición de filtros usando una única clase

// Con la anotación @Order podemos especificar el orden de ejecución de los filtros
// En general, el Pre-filtro con menor índice se ejecuta el primero y el Post-filtro con menor índice se ejecuta el último

@Configuration
public class GlobalFiltersConfiguration {
	private final static Logger logger = LoggerFactory.getLogger(GlobalFiltersConfiguration.class);
	
	// Este método crea un Pre-filtro y un Post-filtro porque hay código tanto antes como después de la sentencia "chain.filter(exchange)" 
	// Esta anotación hace que el Pre-filtro de este método se ejecute en primer lugar y el Post-filtro de este método en último lugar(orden inverso al Pre-filtro de este método)
	@Order(1)
	@Bean
	public GlobalFilter secondFilter() {
		return (exchange, chain) -> {
			logger.info("My second global Pre-filter is executed...");
			return chain.filter(exchange)
					.then(Mono.fromRunnable(() -> logger.info("My third Post-filter is executed...")));
		};
	}
	
	// Este método crea un Pre-filtro y un Post-filtro porque hay código tanto antes como después de la sentencia "chain.filter(exchange)" 
	// Esta anotación hace que el Pre-filtro de este método se ejecute en segundo lugar y el Post-filtro de este método antes del Post-filtro del método "secondFilter"(orden inverso al Pre-filtro de este método)
	@Order(2)
	@Bean
	public GlobalFilter thirdFilter() {
		return (exchange, chain) -> {
			logger.info("My third global Pre-filter is executed...");
			return chain.filter(exchange)
					.then(Mono.fromRunnable(() -> logger.info("My second Post-filter is executed...")));
		};
	}
	
	// Este método crea un Pre-filtro y un Post-filtro porque hay código tanto antes como después de la sentencia "chain.filter(exchange)" 
	// Esta anotación hace que el Pre-filtro de este método se ejecute en tercer y último lugar y el Post-filtro de este método antes del Post-filtro del método "thirdFilter"(orden inverso al Pre-filtro de este método)
	@Order(3)
	@Bean
	public GlobalFilter fourthFilter() {
		return (exchange, chain) -> {
			logger.info("My fourth global Pre-filter is executed...");
			return chain.filter(exchange)
					.then(Mono.fromRunnable(() -> logger.info("My first Post-filter is executed...")));
		};
	}
}
