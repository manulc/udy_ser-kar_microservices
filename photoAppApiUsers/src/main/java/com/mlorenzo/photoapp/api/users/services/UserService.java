package com.mlorenzo.photoapp.api.users.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.mlorenzo.photoapp.api.users.ui.models.requests.CreateUserRequestModel;
import com.mlorenzo.photoapp.api.users.ui.models.responses.CreateUserResponseModel;
import com.mlorenzo.photoapp.api.users.ui.models.responses.UserResponseModel;

public interface UserService extends UserDetailsService {
	CreateUserResponseModel createUser(CreateUserRequestModel userDetails);
	UserResponseModel getUserDetailsByEmail(String email);
	UserResponseModel getUserByUserId(String userId);
}
