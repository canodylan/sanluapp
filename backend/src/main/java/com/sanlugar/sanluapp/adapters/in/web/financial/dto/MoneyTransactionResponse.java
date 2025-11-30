package com.sanlugar.sanluapp.adapters.in.web.financial.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.sanlugar.sanluapp.domain.model.ClubAccount;
import com.sanlugar.sanluapp.domain.model.MoneyCategory;
import com.sanlugar.sanluapp.domain.model.MoneyTransaction;
import com.sanlugar.sanluapp.domain.model.MoneyTransactionType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoneyTransactionResponse {
    private Long id;
    private MoneyTransactionType type;
    private BigDecimal amount;
    private String description;
    private CategorySummary category;
    private AccountSummary accountFrom;
    private AccountSummary accountTo;
    private UserSummaryDto createdBy;
    private LocalDate transactionDate;
    private LocalDateTime createdAt;
    private Long relatedExpenseId;

    public static MoneyTransactionResponse from(MoneyTransaction transaction) {
        return MoneyTransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .category(CategorySummary.from(transaction.getCategory(), transaction.getCategoryId()))
                .accountFrom(AccountSummary.from(transaction.getAccountFrom(), transaction.getAccountFromId()))
                .accountTo(AccountSummary.from(transaction.getAccountTo(), transaction.getAccountToId()))
                .createdBy(UserSummaryDto.from(transaction.getCreatedByUser()))
                .transactionDate(transaction.getTransactionDate())
                .createdAt(transaction.getCreatedAt())
                .relatedExpenseId(transaction.getRelatedEntityId())
                .build();
    }

    @Data
    @Builder
    public static class AccountSummary {
        private Long id;
        private String name;
        private BigDecimal currentBalance;

        public static AccountSummary from(ClubAccount account, Long fallbackId) {
            if (account == null && fallbackId == null) {
                return null;
            }
            return AccountSummary.builder()
                    .id(account != null ? account.getId() : fallbackId)
                    .name(account != null ? account.getName() : null)
                    .currentBalance(account != null ? account.getCurrentBalance() : null)
                    .build();
        }
    }

    @Data
    @Builder
    public static class CategorySummary {
        private Long id;
        private String name;
        private String color;

        public static CategorySummary from(MoneyCategory category, Long fallbackId) {
            if (category == null && fallbackId == null) {
                return null;
            }
            return CategorySummary.builder()
                    .id(category != null ? category.getId() : fallbackId)
                    .name(category != null ? category.getName() : null)
                    .color(category != null ? category.getColor() : null)
                    .build();
        }
    }
}
