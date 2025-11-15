package com.sanlugar.sanluapp.adapters.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanlugar.sanluapp.adapters.in.web.auth.dto.LoginRequest;
import com.sanlugar.sanluapp.adapters.in.web.auth.dto.LoginResponse;
import com.sanlugar.sanluapp.adapters.in.web.auth.dto.RefreshRequest;
import com.sanlugar.sanluapp.adapters.in.web.auth.dto.RefreshResponse;
import com.sanlugar.sanluapp.application.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        LoginResponse res = authService.login(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest req) {
        String access = authService.refresh(req.getRefreshToken());
        return ResponseEntity.ok(new RefreshResponse(access));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) RefreshRequest req) {
        if (req != null && req.getRefreshToken() != null) authService.logout(req.getRefreshToken());
        return ResponseEntity.ok().build();
    }
}
