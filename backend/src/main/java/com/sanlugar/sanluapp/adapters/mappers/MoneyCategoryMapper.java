package com.sanlugar.sanluapp.adapters.mappers;

import com.sanlugar.sanluapp.adapters.out.persistence.MoneyCategoryEntity;
import com.sanlugar.sanluapp.domain.model.MoneyCategory;

public final class MoneyCategoryMapper {

    private MoneyCategoryMapper() {}

    public static MoneyCategory toDomain(MoneyCategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return MoneyCategory.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    public static MoneyCategoryEntity toEntity(MoneyCategory domain) {
        if (domain == null) {
            return null;
        }
        return MoneyCategoryEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .build();
    }
}
