package com.sanlugar.sanluapp.adapters.in.web.financial.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateMoneyExpenseRequest {
    @Size(max = 255)
    private String description;

    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal amount;

    @Size(max = 500)
    private String receiptUrl;
}
