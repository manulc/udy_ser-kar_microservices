package com.mlorenzo.photoapp.api.users.services;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mlorenzo.photoapp.api.users.ui.models.responses.AlbumResponseModel;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class AlbumServiceRestTemplateImpl implements AlbumService {
	private RestTemplate restTemplate;
	private Environment env;
	
	public AlbumServiceRestTemplateImpl(RestTemplate restTemplate, Environment env) {
		this.restTemplate = restTemplate;
		this.env = env;
	}
	
	// Habilita el módulo Circuit Breaker de Resilience4j en este método y se le asocia el nombre "album-ws" como identificador para poder establecer su configuración en el archivo de propiedades
	@CircuitBreaker(name = "albums-ws", fallbackMethod = "getUserAlbumsFb")
	@Override
	public List<AlbumResponseModel> getUserAlbums(String userId) {
		String url = env.getProperty("albums.url");
		ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {}, userId);
		return albumsListResponse.getBody();
	}

	// Método Fallback - Debe tener el mismo prototipo(mismos parámetros de entrada y mismo retorno) que el método donde se aplica el Circuit Breaker
	// Además, con Resilience4j, tiene que haber otro parámetro de entrada en el método de fallback que se corresponda con la excepción(Puede ser más genérica o más concreta) ocurrida en la comunicación
	private List<AlbumResponseModel> getUserAlbumsFb(String userId, Throwable exception) {
		System.out.println("Param = " + userId);
		System.out.println("Exception took place: " + exception.getMessage());
		return List.of();
	}
}
