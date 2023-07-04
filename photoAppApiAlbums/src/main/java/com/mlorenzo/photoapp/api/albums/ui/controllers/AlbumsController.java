package com.mlorenzo.photoapp.api.albums.ui.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mlorenzo.photoapp.api.albums.services.AlbumsService;
import com.mlorenzo.photoapp.api.albums.ui.models.responses.AlbumResponseModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/users/{id}/albums")
public class AlbumsController {
	private final static Logger logger = LoggerFactory.getLogger(AlbumsController.class);
    
    @Autowired
    private AlbumsService albumsService;
    
    // Nota: Como se incluye la librería "jackson-dataformat-xml" en este proyecto, por defecto se admiten peticiones(Cabecera "Content-Type") y
 	// respuestas(Cabecera "Accept") http tanto en formato Json como en formato Xml. Si no se indican las cabeceras correspondiente, por
 	// defecto se utiliza el formato Json. Podemos indicar los formatos admitidos o establecer el formato por defecto(poniendo el "MediaType"
 	// correspondiente en primer lugar) usando los atributos "consumes" y "produces" de las anotaciones @GetMapping, @PostMapping, etc...,
 	// en cada método "handler" de cada controlador.
    
    // En este caso, como no hemos indicado nada en los atributos "consumes" y "produces" de la anotación @GetMapping, se admiten ámbos formatos, Json y Xml, y por defecto se usa
 	// el formato Json si no existen las cabeceras "Content-Type" y "Accept".
    @GetMapping
    public List<AlbumResponseModel> userAlbums(@PathVariable String id) {
        List<AlbumResponseModel> albumsModels = albumsService.getAlbums(id);
        logger.info("Returning " + albumsModels.size() + " albums");
        return albumsModels;
    }
}
