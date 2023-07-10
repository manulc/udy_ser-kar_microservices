package com.mlorenzo.photoapp.api.users.clients;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mlorenzo.photoapp.api.users.ui.models.responses.AlbumResponseModel;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;


// El atributo "name" de esta anotación tiene que tener el nombre del servicio, o microservicio, con el que queremos comunicarnos(Debe coincidir con el nombre indicado en la propiedad "spring.application.name" de dicho servicio o microservicio)
@FeignClient(name = "albums-ws")
public interface AlbumsServiceFeignClient {

	// Nota: Cuando usamos varios módulos de Resilience4j juntos como en este caso(módulos Retry y CircuitBreaker), éstos se ejecutan en un determinado orden por defecto
	// Por defecto, primero se ejeucta el módulo CircuitBreaker y después se ejecuta el módulo Retry(En el application.properties se ha establecido una configuración para que primero se ejecute el módulo Retry y después el módulo CircuitBreaker)
	// Si usamos los módulos Retry y CircuitBreaker conjuntamente y queremos indicar un método de fallback, éste método debe indicarse únicamente en la anotación del módulo CircuitBreaker
	// Habilita el módulo Retry de Resilience4j en este método y se le asocia el nombre "album-ws" como identificador para poder establecer su configuración en el archivo de propiedades
	@Retry(name = "albums-ws")
	// Habilita el módulo Circuit Breaker de Resilience4j en este método y se le asocia el nombre "album-ws" como identificador para poder establecer su configuración en el archivo de propiedades
	@CircuitBreaker(name = "albums-ws", fallbackMethod = "getAlbumsFallback")
	@GetMapping("${albums.path}")
	public List<AlbumResponseModel> getAlbums(@PathVariable String userId);
	
	// Método Fallback - Debe tener el mismo prototipo(mismos parámetros de entrada y mismo retorno) que el método donde se aplica el Circuit Breaker
	// Además, con Resilience4j, tiene que haber otro parámetro de entrada en el método de fallback que se corresponda con la excepción(Puede ser más genérica o más concreta) ocurrida en la comunicación
	default List<AlbumResponseModel> getAlbumsFallback(String userId, Throwable exception) {
		System.out.println("Param = " + userId);
		System.out.println("Exception took place: " + exception.getMessage());
		return Collections.emptyList();
	}
}
