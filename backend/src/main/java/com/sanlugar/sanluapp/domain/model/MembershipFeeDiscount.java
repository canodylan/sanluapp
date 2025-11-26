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
public class MembershipFeeDiscount {
    private Long id;
    private Long membershipFeeId;
    private String concept;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
