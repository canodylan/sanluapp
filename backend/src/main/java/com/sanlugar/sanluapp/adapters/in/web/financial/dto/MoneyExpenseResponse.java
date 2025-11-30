package com.sanlugar.sanluapp.adapters.in.web.financial.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sanlugar.sanluapp.domain.model.MoneyExpense;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoneyExpenseResponse {
    private Long id;
    private String description;
    private BigDecimal amount;
    private String receiptUrl;
    private Long categoryId;
    private MoneyCategoryResponse category;
    private Long accountId;
    private ClubAccountResponse account;
    private UserSummaryDto requestedBy;
    private Boolean approved;
    private UserSummaryDto approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private Long transactionId;
    private MoneyTransactionResponse transaction;

    public static MoneyExpenseResponse from(MoneyExpense expense) {
        return MoneyExpenseResponse.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .receiptUrl(expense.getReceiptUrl())
                .categoryId(expense.getCategoryId())
                .category(expense.getCategory() == null ? null : MoneyCategoryResponse.from(expense.getCategory()))
                .accountId(expense.getAccountId())
                .account(expense.getAccount() == null ? null : ClubAccountResponse.from(expense.getAccount()))
                .requestedBy(UserSummaryDto.from(expense.getRequestedByUser()))
                .approved(expense.getApproved())
                .approvedBy(UserSummaryDto.from(expense.getApprovedByUser()))
                .approvedAt(expense.getApprovedAt())
                .createdAt(expense.getCreatedAt())
                .transactionId(expense.getTransactionId())
                .transaction(expense.getTransaction() == null ? null : MoneyTransactionResponse.from(expense.getTransaction()))
                .build();
    }
}
