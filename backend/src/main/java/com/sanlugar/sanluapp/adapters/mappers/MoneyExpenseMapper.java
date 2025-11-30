package com.sanlugar.sanluapp.adapters.mappers;

import java.util.function.Function;

import com.sanlugar.sanluapp.adapters.out.persistence.ClubAccountEntity;
import com.sanlugar.sanluapp.adapters.out.persistence.MoneyCategoryEntity;
import com.sanlugar.sanluapp.adapters.out.persistence.MoneyExpenseEntity;
import com.sanlugar.sanluapp.adapters.out.persistence.MoneyTransactionEntity;
import com.sanlugar.sanluapp.adapters.out.persistence.UserEntity;
import com.sanlugar.sanluapp.domain.model.MoneyExpense;

public final class MoneyExpenseMapper {

    private MoneyExpenseMapper() {}

    public static MoneyExpense toDomain(MoneyExpenseEntity entity) {
        if (entity == null) {
            return null;
        }
        return MoneyExpense.builder()
                .id(entity.getId())
                .transactionId(entity.getTransaction() != null ? entity.getTransaction().getId() : null)
                .transaction(MoneyTransactionMapper.toDomain(entity.getTransaction()))
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .receiptUrl(entity.getReceiptUrl())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .category(MoneyCategoryMapper.toDomain(entity.getCategory()))
                .accountId(entity.getAccount() != null ? entity.getAccount().getId() : null)
                .account(ClubAccountMapper.toDomain(entity.getAccount()))
                .requestedBy(entity.getRequestedBy() != null ? entity.getRequestedBy().getId() : null)
                .requestedByUser(FinancialUserMapper.toSummary(entity.getRequestedBy()))
                .approved(entity.getApproved())
                .approvedBy(entity.getApprovedBy() != null ? entity.getApprovedBy().getId() : null)
                .approvedByUser(FinancialUserMapper.toSummary(entity.getApprovedBy()))
                .approvedAt(entity.getApprovedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static MoneyExpenseEntity toEntity(
            MoneyExpense domain,
            Function<Long, UserEntity> userResolver,
            Function<Long, MoneyTransactionEntity> transactionResolver,
            Function<Long, MoneyCategoryEntity> categoryResolver,
            Function<Long, ClubAccountEntity> accountResolver) {
        if (domain == null) {
            return null;
        }
        MoneyExpenseEntity entity = MoneyExpenseEntity.builder()
                .id(domain.getId())
                .description(domain.getDescription())
                .amount(domain.getAmount())
                .receiptUrl(domain.getReceiptUrl())
                .approved(domain.getApproved())
                .approvedAt(domain.getApprovedAt())
                .createdAt(domain.getCreatedAt())
                .build();

        if (domain.getRequestedBy() != null && userResolver != null) {
            entity.setRequestedBy(userResolver.apply(domain.getRequestedBy()));
        }
        if (domain.getApprovedBy() != null && userResolver != null) {
            entity.setApprovedBy(userResolver.apply(domain.getApprovedBy()));
        }
        if (domain.getTransactionId() != null && transactionResolver != null) {
            entity.setTransaction(transactionResolver.apply(domain.getTransactionId()));
        }
        if (domain.getCategoryId() != null && categoryResolver != null) {
            entity.setCategory(categoryResolver.apply(domain.getCategoryId()));
        }
        if (domain.getAccountId() != null && accountResolver != null) {
            entity.setAccount(accountResolver.apply(domain.getAccountId()));
        }

        return entity;
    }
}
