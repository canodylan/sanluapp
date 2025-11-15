package com.sanlugar.sanluapp.application.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanlugar.sanluapp.adapters.in.web.auth.dto.LoginResponse;
import com.sanlugar.sanluapp.adapters.in.web.auth.dto.RegisterRequest;
import com.sanlugar.sanluapp.adapters.in.web.auth.dto.UserDto;
import com.sanlugar.sanluapp.adapters.mappers.UserMapper;
import com.sanlugar.sanluapp.domain.model.User;
import com.sanlugar.sanluapp.domain.port.UserRepository;
import com.sanlugar.sanluapp.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultAuthService implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .nickname(request.getNickname())
                .name(request.getName())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .joinAt(LocalDate.now())
                .build();

        User saved = userRepository.save(newUser);
        return buildLoginResponse(saved);
    }

    @Override
    public LoginResponse login(String username, String password) {
        Optional<User> ou = userRepository.findByUsername(username);
        if (ou.isEmpty()) throw new RuntimeException("Invalid credentials");
        User user = ou.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        return buildLoginResponse(user);
    }

    @Override
    public String refresh(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return jwtService.generateAccessToken(UserMapper.toEntity(user));
    }

    @Override
    public void logout(String refreshToken) {
        // stateless implementation: nothing to do. If you store refresh tokens, remove it here.
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new RuntimeException("El usuario es obligatorio");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("El email es obligatorio");
        }
    }

    private LoginResponse buildLoginResponse(User user) {
        String access = jwtService.generateAccessToken(UserMapper.toEntity(user));
        String refresh = jwtService.generateRefreshToken(UserMapper.toEntity(user));
        UserDto dto = UserMapper.toDto(user);
        return new LoginResponse(access, refresh, dto);
    }
}
