package com.mlorenzo.photoapp.api.gateway;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

// Este clase es un filtro global para todas las peticiones http que llegan a este API Gateway y se ejecuta antes de enrutar esas peticiones al servicio, o microservicio, destino
// Es un Pre-filtro porque el código de la implementación del filtro(método "filter") se ejecuta antes de la sentencia "chain.filter(exchange)"
// Esta sentencia, "chain.filter(exchange)", se encarga de enrutar la petición http al servicio, o microservicio, destino

// La interfaz "Ordered" nos permite especificar el orden de ejecución de un filtro
// En general, el Pre-filtro con menor índice se ejecuta el primero y el Post-filtro con menor índice se ejecuta el último

@Component
public class MyPreFilter implements GlobalFilter, Ordered {
	private final static Logger logger = LoggerFactory.getLogger(MyPreFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		logger.info("My first Pre-filter is executed...");
		String requestPath = exchange.getRequest().getPath().toString();
		logger.info("Request path = {}", requestPath);
		HttpHeaders headers = exchange.getRequest().getHeaders();
		Set<String> headerNames = headers.keySet();
		headerNames.forEach(headerName -> {
			String headerValue = headers.getFirst(headerName);
			logger.info("{} - {}", headerName, headerValue);
		});
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
