package com.sanlugar.sanluapp.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.sanlugar.sanluapp.domain.model.MembershipFee;
import com.sanlugar.sanluapp.domain.model.MembershipFeeStatus;

public interface MembershipFeeService {

    MembershipFee createMembershipFeeForYear(Long userId, Integer year, List<LocalDate> attendanceDates);

    MembershipFee applyDiscount(Long membershipFeeId, String concept, BigDecimal amount);

    BigDecimal calculateBaseAmount(int daysAttending);

    MembershipFee recalculateDiscountTotal(Long membershipFeeId);

    MembershipFee markFeeAsPaid(Long membershipFeeId, java.time.LocalDateTime paidAt);

    default MembershipFee markFeeAsPaid(Long membershipFeeId) {
        return markFeeAsPaid(membershipFeeId, null);
    }

    List<MembershipFee> getFeesForUser(Long userId);

    List<MembershipFee> getFeesByStatus(MembershipFeeStatus status);
}
