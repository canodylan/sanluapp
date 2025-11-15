package com.sanlugar.sanluapp.domain.port;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.User;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User save(User user);
    List<User> findAll();
    void deleteById(Long id);
}
