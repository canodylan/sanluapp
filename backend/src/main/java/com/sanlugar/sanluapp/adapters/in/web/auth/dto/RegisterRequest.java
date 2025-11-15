package com.sanlugar.sanluapp.adapters.in.web.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String name;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
