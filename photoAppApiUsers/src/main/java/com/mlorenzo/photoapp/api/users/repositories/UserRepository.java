package com.mlorenzo.photoapp.api.users.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.mlorenzo.photoapp.api.users.data.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
	Optional<UserEntity> findByEmail(String email);
	Optional<UserEntity> findByUserId(String userId);
}
