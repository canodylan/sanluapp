package com.sanlugar.sanluapp.adapters.in.web.financial.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sanlugar.sanluapp.domain.model.MoneyTransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MoneyTransactionRequest {
    @NotNull
    private MoneyTransactionType type;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal amount;

    @Size(max = 255)
    private String description;

    private Long categoryId;
    private Long accountFromId;
    private Long accountToId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;

    @NotNull
    private Long createdBy;

    private Long relatedExpenseId;
}
