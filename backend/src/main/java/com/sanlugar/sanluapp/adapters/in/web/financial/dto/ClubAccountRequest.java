package com.sanlugar.sanluapp.adapters.in.web.financial.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClubAccountRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 1000)
    private String description;

    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal currentBalance;

    private Boolean primary;
}
