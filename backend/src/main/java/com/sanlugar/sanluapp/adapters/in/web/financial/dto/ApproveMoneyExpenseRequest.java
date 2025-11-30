package com.sanlugar.sanluapp.adapters.in.web.financial.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ApproveMoneyExpenseRequest {
    @NotNull
    private Long approvedBy;

    @Positive
    private Long categoryId;

    @Positive
    private Long accountId;
}
