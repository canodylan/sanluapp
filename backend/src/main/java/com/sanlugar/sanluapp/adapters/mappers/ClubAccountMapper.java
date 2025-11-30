package com.sanlugar.sanluapp.adapters.mappers;

import java.math.BigDecimal;

import com.sanlugar.sanluapp.adapters.out.persistence.ClubAccountEntity;
import com.sanlugar.sanluapp.domain.model.ClubAccount;

public final class ClubAccountMapper {

    private ClubAccountMapper() {}

    public static ClubAccount toDomain(ClubAccountEntity entity) {
        if (entity == null) {
            return null;
        }
        return ClubAccount.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .currentBalance(entity.getCurrentBalance())
                .createdAt(entity.getCreatedAt())
            .primary(entity.getPrimaryAccount())
                .build();
    }

    public static ClubAccountEntity toEntity(ClubAccount domain) {
        if (domain == null) {
            return null;
        }
        return ClubAccountEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .currentBalance(domain.getCurrentBalance() == null ? BigDecimal.ZERO : domain.getCurrentBalance())
                .createdAt(domain.getCreatedAt())
                .primaryAccount(Boolean.TRUE.equals(domain.getPrimary()))
                .build();
    }
}
