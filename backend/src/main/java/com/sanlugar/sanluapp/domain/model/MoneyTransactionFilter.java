package com.sanlugar.sanluapp.domain.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyTransactionFilter {
    private MoneyTransactionType type;
    private Long accountId;
    private Long categoryId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long createdBy;
}
