package com.mlorenzo.app.ws.ui.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mlorenzo.app.ws.services.UserService;
import com.mlorenzo.app.ws.ui.models.requests.UpdateUserDetailsRequestModel;
import com.mlorenzo.app.ws.ui.models.requests.UserDetailsRequestModel;
import com.mlorenzo.app.ws.ui.models.responses.UserRest;

@RestController
@RequestMapping("/users") // http://localhost:8080/users
public class UserController {
	
	@Autowired
	private UserService userService;
	
	// Nota: Como se incluye la librería "jackson-dataformat-xml" en este proyecto, por defecto se admiten peticiones(Cabecera "Content-Type") y
	// respuestas(Cabecera "Accept") http tanto en formato Json como en formato Xml. Si no se indican las cabeceras correspondiente, por
	// defecto se utiliza el formato Json. Podemos indicar los formatos admitidos o establecer el formato por defecto(poniendo el "MediaType"
	// correspondiente en primer lugar) usando los atributos "consumes" y "produces" de las anotaciones @GetMapping, @PostMapping, etc...,
	// en cada método "handler" de cada controlador.
	
	// Por defecto los parámetros anotados con @RequestParam son obligatorios
	// Si definimos un parámetro usando el atributo defaultValue de la anotación @RequestParam, ese atributo pasa a ser opcional
	// Otra forma de hacer un parámetro opcional es usando el atributo required de la anotación @RequestParam con valor false, pero sólo funciona con parámetros que son objetos(no primitivos), ya que es la única forma de asignarles el valor nulo o null 
	@GetMapping("/params")
	public String getUserWithParams(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "50") int limit,
			@RequestParam(value = "sort", required = false) String sort) {
		if(sort == null)
			sort = "desc";
		return "get users was called with page=" + page + " and limit=" + limit + " and sort=" + sort;
	}
	
	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<List<UserRest>> getUsers() {
		return ResponseEntity.ok().body(userService.getUsers());
	}
	
	@GetMapping(path = "/{userId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<UserRest> getUserById(@PathVariable String userId) {
		Optional<UserRest> oUser = userService.getUserById(userId);
		if(oUser.isPresent())
			return new ResponseEntity<>(oUser.get(), HttpStatus.OK);
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
				 produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<UserRest> createUser(@RequestBody @Valid UserDetailsRequestModel userDetails) {		
		return new ResponseEntity<UserRest>(userService.createUser(userDetails), HttpStatus.CREATED);
	}
	
	@PutMapping(path = "/{userId}",
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<UserRest> updateUser(@PathVariable(name = "userId") String id, @RequestBody @Valid UpdateUserDetailsRequestModel userDetails) {
		Optional<UserRest> oUser = userService.getUserById(id);
		if(!oUser.isPresent())
			return ResponseEntity.notFound().build();
		return ResponseEntity.ok(userService.updateUser(oUser.get(), userDetails));
	}
	
	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
		Optional<UserRest> oUser = userService.getUserById(userId);
		if(!oUser.isPresent())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		userService.deleteUser(oUser.get());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
