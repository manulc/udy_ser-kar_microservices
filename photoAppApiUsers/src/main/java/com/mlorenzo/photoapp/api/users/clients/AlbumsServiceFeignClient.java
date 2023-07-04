package com.mlorenzo.photoapp.api.users.clients;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mlorenzo.photoapp.api.users.ui.models.responses.AlbumResponseModel;

import feign.FeignException;
import feign.hystrix.FallbackFactory;


// El atributo "name" de esta anotación tiene que tener el nombre del servicio, o microservicio, con el que queremos comunicarnos(Debe coincidir con el nombre indicado en la propiedad "spring.application.name" de dicho servicio o microservicio)
// Los atributos "fallback" y "fallbackFactory" son para el patrón Circuit Breaker(Implementado en nuestro caso por Hystrix). En caso de que el servicio o microservicio "albums-ws" esté caído, se ejecutará el camino alternativo implementado en nuestra clase "AlbumsFallback"
// El atributo "fallback" nos permite implementar un camino alternativo sin la posibilidad de manejar las excepciones que pueda lanzar el cliente Feign, Sin embargo, el atributo "fallbackFactory" sí nos permite manejar esas excepciones
//@FeignClient(name = "albums-ws", fallback = AlbumsFallback.class)
@FeignClient(name = "albums-ws", fallbackFactory = AlbumsFallbackFactory.class)
public interface AlbumsServiceFeignClient {

	@GetMapping("${albums.path}")
	public List<AlbumResponseModel> getAlbums(@PathVariable String userId);
}

// Clase que implementa el camino alternativo para usar en el patrón Circuit Breaker en caso de que el servicio o microservicio "albums-ws" no responda
// A diferencia de la clase "AlbumsServiceClientFallback", esta clase no maneja las excepciones que pueda lanzar, o causar, el cliente Feign

// Se comenta porque ahora usamos el atributo "fallbackFactory" del cliente Feign en lugar del atributo "fallback"
// Creamos un bean de Spring de esta clase sólo si se usa el atributo "fallback" del cliente Feign para el patrón Circuit Breaker
//@Component
class AlbumsFallback implements AlbumsServiceFeignClient {

	@Override
	public List<AlbumResponseModel> getAlbums(String id) {
		return new ArrayList<>();
	}
}

// Clase que implementa el camino alternativo para usar en el patrón Circuit Breaker en caso de que el servicio o microservicio "albums-ws" no responda
// A diferencia de la clase "AlbumsFallback", esta clase maneja las excepciones que pueda lanzar, o causar, el cliente Feign

//Creamos un bean de Spring de esta clase sólo si se usa el atributo "fallbackFactory" del cliente Feign para el patrón Circuit Breaker
@Component
class AlbumsFallbackFactory implements FallbackFactory<AlbumsServiceFeignClient> {

	@Override
	public AlbumsServiceFeignClient create(Throwable cause) {
		return new AlbumsServiceFeignClientFallback(cause);
	}
	
}

class AlbumsServiceFeignClientFallback implements AlbumsServiceFeignClient {
	private static final Logger logger = LoggerFactory.getLogger(AlbumsServiceFeignClientFallback.class);
	
	private Throwable cause;

	public AlbumsServiceFeignClientFallback(Throwable cause) {
		this.cause = cause;
	}

	@Override
	public List<AlbumResponseModel> getAlbums(String id) {
		String errorMessage = cause.getLocalizedMessage() != null ? cause.getLocalizedMessage() : cause.getMessage();
		if(cause instanceof FeignException && ((FeignException)cause).status() == 404 )
			logger.error("404 error took place when getAlbums was called with userId={}. Error message: {}", id, errorMessage);
		else
			logger.error("Other error took place: {}", errorMessage);
		return new ArrayList<>();
	}
}
