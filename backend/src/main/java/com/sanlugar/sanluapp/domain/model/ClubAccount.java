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
public class ClubAccount {
    private Long id;
    private String name;
    private String description;
    @Builder.Default
    private BigDecimal currentBalance = BigDecimal.ZERO;
    private LocalDateTime createdAt;
    @Builder.Default
    private Boolean primary = Boolean.FALSE;
}
