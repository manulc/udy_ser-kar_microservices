package com.mlorenzo.photoapp.api.users.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import feign.Response;
import feign.codec.ErrorDecoder;

// Clase para manejar, de manera centralizada en un sitio, los errores devueltos por el cliente Feign

// Nota: Esta clase no haría falta si se manejan los errores devueltos por el cliente Feign usando el patrón Circuit Breaker(ver atributo "fallbackFactory" de la anotación "@FeignClient" en la clase "AlbumsServiceClient"

// Se comenta porque ahora usamos el manejador de errores producidos por el cliente Feign usando el patrón Circuit Breaker
//@Component
public class FeignErrorDecoder implements ErrorDecoder {
	
	@Value("${albums.exceptions.albums-not-found}")
	private String errorMessage;

	@Override
	public Exception decode(String methodKey, Response response) {
		switch(response.status()) {
			case 400:
				// return new BadrequestException();
				break;
			case 404:
				if(methodKey.contains("getAlbums"))
					return new ResponseStatusException(HttpStatus.valueOf(response.status()), errorMessage);
				break;
			default:
				return new Exception(response.reason());
		}
		return null;
	}
}
