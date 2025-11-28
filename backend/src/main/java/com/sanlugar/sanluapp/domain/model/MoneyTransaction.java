package com.sanlugar.sanluapp.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyTransaction {
    private Long id;
    private MoneyTransactionType type;
    private BigDecimal amount;
    private String description;
    private Long categoryId;
    private Long accountFromId;
    private Long accountToId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDate transactionDate;
    private String relatedEntityType;
    private Long relatedEntityId;

    private MoneyCategory category;
    private ClubAccount accountFrom;
    private ClubAccount accountTo;
    private FinancialUserSummary createdByUser;
}
