package com.mlorenzo.photoapp.api.users.services;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mlorenzo.photoapp.api.users.ui.models.responses.AlbumResponseModel;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class AlbumServiceRestTemplateImpl implements AlbumService {
	private RestTemplate restTemplate;
	private Environment env;
	
	public AlbumServiceRestTemplateImpl(RestTemplate restTemplate, Environment env) {
		this.restTemplate = restTemplate;
		this.env = env;
	}
	
	@HystrixCommand(fallbackMethod = "getUserAlbumsFb")
	@Override
	public List<AlbumResponseModel> getUserAlbums(String userId) {
		String url = env.getProperty("albums.url");
		ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {}, userId);
		return albumsListResponse.getBody();
	}

	private List<AlbumResponseModel> getUserAlbumsFb(String userId) {
		return List.of();
	}
}
