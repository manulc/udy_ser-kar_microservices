package com.mlorenzo.app.ws.services;

import java.util.List;
import java.util.Optional;

import com.mlorenzo.app.ws.ui.models.requests.UpdateUserDetailsRequestModel;
import com.mlorenzo.app.ws.ui.models.requests.UserDetailsRequestModel;
import com.mlorenzo.app.ws.ui.models.responses.UserRest;

public interface UserService {
	List<UserRest> getUsers();
	Optional<UserRest> getUserById(String userId);
	UserRest createUser(UserDetailsRequestModel userDetails);
	UserRest updateUser(UserRest userRest, UpdateUserDetailsRequestModel userDetails);
	void deleteUser(UserRest userRest);
}
