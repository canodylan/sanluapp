package com.sanlugar.sanluapp.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyExpense {
    private Long id;
    private Long transactionId;
    private MoneyTransaction transaction;
    private String description;
    private BigDecimal amount;
    private String receiptUrl;
    private Long requestedBy;
    private FinancialUserSummary requestedByUser;
    @Builder.Default
    private Boolean approved = Boolean.FALSE;
    private Long approvedBy;
    private FinancialUserSummary approvedByUser;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}
