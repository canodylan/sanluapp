package com.sanlugar.sanluapp.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.StringUtils;

import com.sanlugar.sanluapp.domain.model.Role;
import com.sanlugar.sanluapp.domain.model.User;
import com.sanlugar.sanluapp.domain.port.UserRepository;
import com.sanlugar.sanluapp.domain.port.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

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
                existing.setRoles(resolveRoles(user.getRoles()));
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

    private Set<Role> resolveRoles(Set<Role> requestedRoles) {
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            return Collections.emptySet();
        }

        return requestedRoles.stream()
                .map(Role::getName)
                .map(this::normalizeRoleName)
                .filter(StringUtils::hasText)
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + name)))
                .collect(Collectors.toSet());
    }

    private String normalizeRoleName(String name) {
        if (!StringUtils.hasText(name)) return null;
        String normalized = name.trim();
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring(5);
        }
        return normalized.toUpperCase();
    }
}
