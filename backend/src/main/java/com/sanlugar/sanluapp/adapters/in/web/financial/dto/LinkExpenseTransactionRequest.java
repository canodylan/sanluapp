package com.sanlugar.sanluapp.adapters.in.web.financial.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LinkExpenseTransactionRequest {
    @NotNull
    private Long transactionId;
}
