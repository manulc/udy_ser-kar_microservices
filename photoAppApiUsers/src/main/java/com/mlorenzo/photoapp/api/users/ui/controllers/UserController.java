package com.mlorenzo.photoapp.api.users.ui.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mlorenzo.photoapp.api.users.services.UserService;
import com.mlorenzo.photoapp.api.users.ui.models.requests.CreateUserRequestModel;
import com.mlorenzo.photoapp.api.users.ui.models.responses.CreateUserResponseModel;
import com.mlorenzo.photoapp.api.users.ui.models.responses.UserResponseModel;

@RestController
@RequestMapping("/users")
public class UserController {
	private UserService userService;
	private Environment env;
	
	public UserController(UserService userService, Environment env) {
		this.userService = userService;
		this.env = env;
	}
	
	// Nota: Como se incluye la librería "jackson-dataformat-xml" en este proyecto, por defecto se admiten peticiones(Cabecera "Content-Type") y
	// respuestas(Cabecera "Accept") http tanto en formato Json como en formato Xml. Si no se indican las cabeceras correspondiente, por
	// defecto se utiliza el formato Json. Podemos indicar los formatos admitidos o establecer el formato por defecto(poniendo el "MediaType"
	// correspondiente en primer lugar) usando los atributos "consumes" y "produces" de las anotaciones @GetMapping, @PostMapping, etc...,
	// en cada método "handler" de cada controlador.
	

	@GetMapping("/status/check")
	public String status(HttpServletRequest request) {
		// En nuestro caso, la expresión "server.port" devuelve 0 y la expresión "local.server.port" devuelve el número de puerto generado
		return "Working on port " + env.getProperty("local.server.port") + ", with token secret = " + env.getProperty("token.secret");
	}
	
	// En este caso, si no se indican las cabeceras "Content-Type" y/o "Accept", por defecto se utiliza el formato Xml porque esta puesto en primer
	// lugar en los atributos "consumes" y "produces"
	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
				 produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public CreateUserResponseModel createUser(@Valid @RequestBody CreateUserRequestModel userModel) {
		return userService.createUser(userModel);
	}
	
	// En este caso, como no hemos indicado nada en los atributos "consumes" y "produces" de la anotación @GetMapping, se admiten ámbos formatos, Json y Xml, y por defecto se usa
	// el formato Json si no existen las cabeceras "Content-Type" y "Accept".
	// "principal" es un objeto del contexto de seguridad de Spring Security que contiene datos del usuario autenticado correctamente en el sistema
	// En este caso, "principal" es el id del usuario autenticado porque en nuestro filtro de seguridad "AuthorizationFilter", que valida el token JWT, se crea un objeto del tipo UsernamePasswordAuthenticationToken de Spring Security usando como objeto "principal" el id del usuario obtenido del "subject" del token JWT
	// "#id" hace referencia al parámetro de entrada "id" de este método y se corresponde con un id de un usuario
	// "returnObject" hace referencia a la respuesta de este método que es de tipo UserResponseModel
	// Sólo se ejecuta este método si el id del usuario autenticado coincide con el id que se le pasa a este método como argumento de entrada
	//@PreAuthorize("principal == #id")
	// Sólo se devuelve el resultado de este método si el id del usuario autenticado coincide con el id del usuario de la respuesta de este método
	@PostAuthorize("principal == returnObject.userId")
	@GetMapping("/{userId}")
	public UserResponseModel getUser(@PathVariable(value = "userId") String id) {
		return userService.getUserByUserId(id);
	}
}
