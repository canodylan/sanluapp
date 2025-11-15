package com.sanlugar.sanluapp.adapters.mappers;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.sanlugar.sanluapp.adapters.out.persistence.RoleEntity;
import com.sanlugar.sanluapp.domain.model.Role;

public final class RoleMapper {

    private RoleMapper() {}

    public static Role toDomain(RoleEntity entity) {
        if (entity == null) return null;
        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public static RoleEntity toEntity(Role role) {
        if (role == null) return null;
        return RoleEntity.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public static Set<Role> toDomainSet(Set<RoleEntity> entities) {
        if (entities == null) return Collections.emptySet();
        return entities.stream().map(RoleMapper::toDomain).collect(Collectors.toSet());
    }

    public static Set<RoleEntity> toEntitySet(Set<Role> roles) {
        if (roles == null) return Collections.emptySet();
        return roles.stream().map(RoleMapper::toEntity).collect(Collectors.toSet());
    }
}
