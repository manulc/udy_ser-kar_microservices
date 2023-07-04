package com.mlorenzo.app.ws.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mlorenzo.app.ws.services.UserService;
import com.mlorenzo.app.ws.shared.Utils;
import com.mlorenzo.app.ws.ui.models.requests.UpdateUserDetailsRequestModel;
import com.mlorenzo.app.ws.ui.models.requests.UserDetailsRequestModel;
import com.mlorenzo.app.ws.ui.models.responses.UserRest;

@Service
public class UserServiceImpl implements UserService {
	private Map<String, UserRest> users = new HashMap<>();
	private Utils utils;
	
	@Autowired // En las últimas versiones de Spring, ya no hace falta poner esta anotación para realizar la inyección de dependencias sobre constructores, es decir, se puede omitir esta anotación y todo funciona correctamente
	public UserServiceImpl(Utils utils) {
		this.utils = utils;
	}
	
	@Override
	public List<UserRest> getUsers() {
		List<UserRest> userRestList = new ArrayList<>();
		users.forEach((key, user) -> userRestList.add(user));
		return userRestList;
	}
	
	@Override
	public Optional<UserRest> getUserById(String userId) {
		return Optional.ofNullable(users.get(userId));
	}

	@Override
	public UserRest createUser(UserDetailsRequestModel userDetails) {
		UserRest user = new UserRest();
		user.setEmail(userDetails.getEmail());
		user.setFirstName(userDetails.getFirstName());
		user.setLastName(userDetails.getLastName());
		String userId = utils.generateUserId();
		user.setUserId(userId);
		users.put(userId, user);
		return user;
	}

	@Override
	public UserRest updateUser(UserRest userRest, UpdateUserDetailsRequestModel userDetails) {
		userRest.setFirstName(userDetails.getFirstName());
		userRest.setLastName(userDetails.getLastName());
		users.put(userRest.getUserId(), userRest);
		return userRest;
	}

	@Override
	public void deleteUser(UserRest userRest) {
		users.remove(userRest.getUserId());
	}
}
