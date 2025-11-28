package com.sanlugar.sanluapp.adapters.mappers;

import com.sanlugar.sanluapp.adapters.out.persistence.UserEntity;
import com.sanlugar.sanluapp.domain.model.FinancialUserSummary;

public final class FinancialUserMapper {

    private FinancialUserMapper() {}

    public static FinancialUserSummary toSummary(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return FinancialUserSummary.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .nickname(entity.getNickname())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .build();
    }
}
