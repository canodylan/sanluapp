package com.sanlugar.sanluapp.adapters.in.web.financial.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MoneyCategoryRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "El color debe estar en formato HEX (#RRGGBB)")
    private String color;
}
