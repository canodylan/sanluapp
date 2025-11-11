package com.sanlugar.sanluapp.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pure domain model for User (no persistence annotations).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private String email;
    private String nickname;
    private String name;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDateTime birthday;
    private LocalDateTime joinAt;
}
