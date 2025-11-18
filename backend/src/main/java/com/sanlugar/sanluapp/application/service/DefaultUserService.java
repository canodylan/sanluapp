package com.sanlugar.sanluapp.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanlugar.sanluapp.domain.model.User;
import com.sanlugar.sanluapp.domain.port.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public User create(User user) {
        // assume user.passwordHash contains the raw password on create -> encode it
        if (user.getPasswordHash() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> update(Long id, User user) {
        return userRepository.findById(id).map(existing -> {
            existing.setUsername(user.getUsername());
            existing.setEmail(user.getEmail());
            existing.setNickname(user.getNickname());
            existing.setName(user.getName());
            existing.setFirstName(user.getFirstName());
            existing.setLastName(user.getLastName());
            existing.setPhoneNumber(user.getPhoneNumber());
            existing.setBirthday(user.getBirthday());
            existing.setJoinAt(user.getJoinAt());
            if (user.getRoles() != null) {
                existing.setRoles(user.getRoles());
            }
            if (user.getPasswordHash() != null && !user.getPasswordHash().isBlank()) {
                existing.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            }
            return userRepository.save(existing);
        });
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
