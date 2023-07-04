package com.mlorenzo.photoapp.api.users.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mlorenzo.photoapp.api.users.clients.AlbumsServiceFeignClient;
import com.mlorenzo.photoapp.api.users.ui.models.responses.AlbumResponseModel;


@Service
public class AlbumServiceFeignImpl implements AlbumService {
	//private static final Logger logger = LoggerFactory.getLogger(AlbumServiceFeignImpl.class);
	
	private AlbumsServiceFeignClient albumsServiceFeignClient;
	
	public AlbumServiceFeignImpl(AlbumsServiceFeignClient albumsServiceFeignClient) {
		this.albumsServiceFeignClient = albumsServiceFeignClient;
	}
	
	@Override
	public List<AlbumResponseModel> getUserAlbums(String userId) {
		// Ya no hace falta este bloque try-catch sobre el cliente Feign porque ahora usamos un manejador de errores centralizado para dicho cliente en la clase "FeignErrorDecoder"
		/*List<AlbumResponseModel> albumsList = Collections.emptyList();
	    try {
			albumsList = albumsServiceFeignClient.getAlbums(userId);
		}
		catch(FeignException e) {
			logger.error(e.getLocalizedMessage());
		}*/
	    return albumsServiceFeignClient.getAlbums(userId);
	}		
}
