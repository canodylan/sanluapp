package com.sanlugar.sanluapp.application.service;

import com.sanlugar.sanluapp.adapters.in.web.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(String username, String password);
    String refresh(String refreshToken);
    void logout(String refreshToken);
}
