package com.sanlugar.sanluapp.application.service;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.User;

public interface UserService {
    User create(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    Optional<User> update(Long id, User user);
    void delete(Long id);
}
