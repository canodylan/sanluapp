package com.sanlugar.sanluapp.adapters.in.web.membership.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PayMembershipFeeRequest {
    private LocalDateTime paidAt;
}
