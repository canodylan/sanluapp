package com.sanlugar.sanluapp.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipFee {
    private Long id;
    private Long userId;
    private Integer year;
    private Integer daysAttending;
    private BigDecimal baseAmount;
    private BigDecimal discountTotal;
    private BigDecimal finalAmount;
    private MembershipFeeStatus status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Set<MembershipFeeDay> attendanceDays = new LinkedHashSet<>();

    @Builder.Default
    private Set<MembershipFeeDiscount> discounts = new LinkedHashSet<>();
}
