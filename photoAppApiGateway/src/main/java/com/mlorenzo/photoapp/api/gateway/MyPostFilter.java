package com.mlorenzo.photoapp.api.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

// Este clase es un filtro global para todas las peticiones http que llegan a este Api Gateway y se ejecuta después de enrutar esas peticiones al servicio, o microservicio, destino
// Es un Post-filtro porque el código de la implementación del filtro(método "filter") se ejecuta a continuación de la sentencia "chain.filter(exchange)"
// Esta sentencia, "chain.filter(exchange)", se encarga de enrutar la petición http al servicio, o microservicio, destino

// La interfaz "Ordered" nos permite especificar el orden de ejecución de un filtro
// En general, el Pre-filtro con menor índice se ejecuta el primero y el Post-filtro con menor índice se ejecuta el último

@Component
public class MyPostFilter implements GlobalFilter, Ordered {
	private final static Logger logger = LoggerFactory.getLogger(MyPostFilter.class);
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		return chain.filter(exchange)
				.then(Mono.fromRunnable(() -> logger.info("My last Post-filter is executed...")));
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
