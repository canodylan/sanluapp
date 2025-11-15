package com.sanlugar.sanluapp.adapters.in.web.auth.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String name;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;
    private LocalDate joinAt;
}
