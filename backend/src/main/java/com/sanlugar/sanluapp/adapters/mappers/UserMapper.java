package com.sanlugar.sanluapp.adapters.mappers;

import java.util.Collections;
import java.util.stream.Collectors;

import com.sanlugar.sanluapp.adapters.in.web.auth.dto.UserDto;
import com.sanlugar.sanluapp.adapters.out.persistence.UserEntity;
import com.sanlugar.sanluapp.domain.model.User;

public final class UserMapper {

    private UserMapper() {}

    public static User toDomain(UserEntity e) {
        if (e == null) return null;
        return User.builder()
                .id(e.getId())
                .username(e.getUsername())
                .passwordHash(e.getPasswordHash())
                .email(e.getEmail())
                .nickname(e.getNickname())
                .name(e.getName())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .phoneNumber(e.getPhoneNumber())
                .birthday(e.getBirthday())
                .joinAt(e.getJoinAt())
                .roles(RoleMapper.toDomainSet(e.getRoles()))
                .build();
    }

    public static UserEntity toEntity(User u) {
        if (u == null) return null;
        return UserEntity.builder()
                .id(u.getId())
                .username(u.getUsername())
                .passwordHash(u.getPasswordHash())
                .email(u.getEmail())
                .nickname(u.getNickname())
                .name(u.getName())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .phoneNumber(u.getPhoneNumber())
                .birthday(u.getBirthday())
                .joinAt(u.getJoinAt())
                .roles(RoleMapper.toEntitySet(u.getRoles()))
                .build();
    }

    public static UserDto toDto(User u) {
        if (u == null) return null;
        return UserDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .nickname(u.getNickname())
                .name(u.getName())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .phoneNumber(u.getPhoneNumber())
                .birthday(u.getBirthday())
                .joinAt(u.getJoinAt())
            .roles(u.getRoles() == null ? Collections.emptySet()
                : u.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()))
                .build();
    }
}
