package com.mlorenzo.photoapp.api.users.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mlorenzo.photoapp.api.users.data.UserEntity;
import com.mlorenzo.photoapp.api.users.repositories.UserRepository;
import com.mlorenzo.photoapp.api.users.ui.models.requests.CreateUserRequestModel;
import com.mlorenzo.photoapp.api.users.ui.models.responses.AlbumResponseModel;
import com.mlorenzo.photoapp.api.users.ui.models.responses.CreateUserResponseModel;
import com.mlorenzo.photoapp.api.users.ui.models.responses.UserResponseModel;


@Service
public class UserServiceImpl implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	private UserRepository userRepository;
	private ModelMapper modelMapper;
	private PasswordEncoder passwordEncoder;
	private AlbumService albumService;
	
	public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder,
			@Qualifier("albumServiceRestTemplateImpl") AlbumService albumService, Environment env) {
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
		this.passwordEncoder = passwordEncoder;
		this.albumService = albumService;
	}

	@Override
	public CreateUserResponseModel createUser(CreateUserRequestModel userModel) {
		UserEntity userEntity = modelMapper.map(userModel, UserEntity.class);
		userEntity.setUserId(UUID.randomUUID().toString());
		// Encriptamos la constraseña con el algoritmo BCrypt
		userEntity.setEncryptedPassword(passwordEncoder.encode(userModel.getPassword()));
		return modelMapper.map(userRepository.save(userEntity), CreateUserResponseModel.class);
	}

	// En este caso en particular, como el usuario no tiene un atributo username propio, se utiliza el email, que es único para cada usuario, como username para localizar sus detalles en la base de datos
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
	}

	// A diferencia del método de arriba "loadUserByUsername", este método devuelve un objeto de tipo "UserDto" en lugar de un objeto de tipo "UserDetails" de Spring Security
	// Necesitamos un objeto de tipo "UserDto" porque contiene el atributo "userId" que se utiliza para el Subject del token JWT. Sin embargo, el objeto de tipo "UserDetails" de Spring Security no contiene dicho atributo
	@Override
	public UserResponseModel getUserDetailsByEmail(String email) {
		UserEntity userEntity = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
		return modelMapper.map(userEntity, UserResponseModel.class);
	}

	@Override
	public UserResponseModel getUserByUserId(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId + " not found"));
		UserResponseModel userModel = modelMapper.map(userEntity, UserResponseModel.class);
		logger.info("Before calling albums microservice");
		List<AlbumResponseModel> albumsList = albumService.getUserAlbums(userId);
		logger.info("After calling albums microservice");
		userModel.setAlbums(albumsList);
		return userModel;
	}
}
