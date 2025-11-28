package com.sanlugar.sanluapp.adapters.in.web.financial.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sanlugar.sanluapp.domain.model.ClubAccount;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClubAccountResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal currentBalance;
    private LocalDateTime createdAt;

    public static ClubAccountResponse from(ClubAccount account) {
        return ClubAccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .description(account.getDescription())
                .currentBalance(account.getCurrentBalance())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
