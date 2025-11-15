package com.sanlugar.sanluapp.application.service;

import com.sanlugar.sanluapp.adapters.in.web.auth.dto.LoginResponse;
import com.sanlugar.sanluapp.adapters.in.web.auth.dto.RegisterRequest;

public interface AuthService {
    LoginResponse register(RegisterRequest request);
    LoginResponse login(String username, String password);
    String refresh(String refreshToken);
    void logout(String refreshToken);
}
