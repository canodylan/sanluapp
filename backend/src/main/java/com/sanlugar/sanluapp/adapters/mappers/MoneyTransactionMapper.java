package com.sanlugar.sanluapp.adapters.mappers;

import java.util.function.Function;

import com.sanlugar.sanluapp.adapters.out.persistence.ClubAccountEntity;
import com.sanlugar.sanluapp.adapters.out.persistence.MoneyCategoryEntity;
import com.sanlugar.sanluapp.adapters.out.persistence.MoneyTransactionEntity;
import com.sanlugar.sanluapp.adapters.out.persistence.UserEntity;
import com.sanlugar.sanluapp.domain.model.MoneyTransaction;

public final class MoneyTransactionMapper {

    private MoneyTransactionMapper() {}

    public static MoneyTransaction toDomain(MoneyTransactionEntity entity) {
        if (entity == null) {
            return null;
        }
        return MoneyTransaction.builder()
                .id(entity.getId())
                .type(entity.getType())
                .amount(entity.getAmount())
                .description(entity.getDescription())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .accountFromId(entity.getAccountFrom() != null ? entity.getAccountFrom().getId() : null)
                .accountToId(entity.getAccountTo() != null ? entity.getAccountTo().getId() : null)
                .createdBy(entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null)
                .createdAt(entity.getCreatedAt())
                .transactionDate(entity.getTransactionDate())
                .relatedEntityType(entity.getRelatedEntityType())
                .relatedEntityId(entity.getRelatedEntityId())
                .category(MoneyCategoryMapper.toDomain(entity.getCategory()))
                .accountFrom(ClubAccountMapper.toDomain(entity.getAccountFrom()))
                .accountTo(ClubAccountMapper.toDomain(entity.getAccountTo()))
                .createdByUser(FinancialUserMapper.toSummary(entity.getCreatedBy()))
                .build();
    }

    public static MoneyTransactionEntity toEntity(
            MoneyTransaction domain,
            Function<Long, ClubAccountEntity> accountResolver,
            Function<Long, MoneyCategoryEntity> categoryResolver,
            Function<Long, UserEntity> userResolver) {
        if (domain == null) {
            return null;
        }
        MoneyTransactionEntity entity = MoneyTransactionEntity.builder()
                .id(domain.getId())
                .type(domain.getType())
                .amount(domain.getAmount())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .transactionDate(domain.getTransactionDate())
                .relatedEntityType(domain.getRelatedEntityType())
                .relatedEntityId(domain.getRelatedEntityId())
                .build();

        if (domain.getCategoryId() != null && categoryResolver != null) {
            entity.setCategory(categoryResolver.apply(domain.getCategoryId()));
        }
        if (domain.getAccountFromId() != null && accountResolver != null) {
            entity.setAccountFrom(accountResolver.apply(domain.getAccountFromId()));
        }
        if (domain.getAccountToId() != null && accountResolver != null) {
            entity.setAccountTo(accountResolver.apply(domain.getAccountToId()));
        }
        if (domain.getCreatedBy() != null && userResolver != null) {
            entity.setCreatedBy(userResolver.apply(domain.getCreatedBy()));
        }

        return entity;
    }
}
