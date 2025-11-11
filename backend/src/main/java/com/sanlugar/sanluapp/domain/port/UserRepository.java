package com.sanlugar.sanluapp.domain.port;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.User;

public interface UserRepository {
    Optional<User> findById(Long id);
    User save(User user);
    List<User> findAll();
    void deleteById(Long id);
}
