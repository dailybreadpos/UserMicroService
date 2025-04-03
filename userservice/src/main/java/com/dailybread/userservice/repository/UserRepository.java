package com.dailybread.userservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dailybread.userservice.model.*;
public interface UserRepository extends MongoRepository<User,String> {

    Optional<User>findByEmail(String email);
    Optional<User> findByActivationToken(String activationToken);
}
