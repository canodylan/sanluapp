package com.sanlugar.sanluapp.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanlugar.sanluapp.domain.model.MembershipFee;
import com.sanlugar.sanluapp.domain.model.MembershipFeeDay;
import com.sanlugar.sanluapp.domain.model.MembershipFeeDiscount;
import com.sanlugar.sanluapp.domain.model.MembershipFeeStatus;
import com.sanlugar.sanluapp.domain.port.MembershipFeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultMembershipFeeService implements MembershipFeeService {

    private static final BigDecimal BASE_FEE = new BigDecimal("0.00"); //TODO
    private static final BigDecimal DAILY_RATE = new BigDecimal("10.00");

    private final MembershipFeeRepository membershipFeeRepository;

    @Override
    public MembershipFee createMembershipFeeForYear(Long userId, Integer year, List<LocalDate> attendanceDates) {
        if (attendanceDates == null || attendanceDates.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un día de asistencia");
        }
        Set<MembershipFeeDay> daysSet = attendanceDates.stream()
            .distinct()
                .map(date -> MembershipFeeDay.builder().attendanceDate(date).build())
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        int days = daysSet.size();
        BigDecimal baseAmount = calculateBaseAmount(days);

        MembershipFee fee = MembershipFee.builder()
                .userId(userId)
                .year(year)
                .daysAttending(days)
                .baseAmount(baseAmount)
                .discountTotal(BigDecimal.ZERO)
            .finalAmount(baseAmount)
                .status(MembershipFeeStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .attendanceDays(daysSet)
                .build();

        return membershipFeeRepository.save(fee);
    }

    @Override
    public MembershipFee applyDiscount(Long membershipFeeId, String concept, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("El descuento debe ser positivo");
        }

        MembershipFee fee = membershipFeeRepository.findById(membershipFeeId)
                .orElseThrow(() -> new IllegalArgumentException("Cuota no encontrada"));

        MembershipFeeDiscount discount = MembershipFeeDiscount.builder()
                .concept(concept)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .build();

        fee.getDiscounts().add(discount);
        fee.setStatus(MembershipFeeStatus.CALCULATED);
        fee.setUpdatedAt(LocalDateTime.now());

        MembershipFee saved = membershipFeeRepository.save(fee);
        return recalculateDiscountTotal(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateBaseAmount(int daysAttending) {
        if (daysAttending <= 0) {
            throw new IllegalArgumentException("Los días de asistencia deben ser mayores que cero");
        }
        return BASE_FEE.add(DAILY_RATE.multiply(BigDecimal.valueOf(daysAttending)));
    }

    @Override
    public MembershipFee recalculateDiscountTotal(Long membershipFeeId) {
        MembershipFee fee = membershipFeeRepository.findById(membershipFeeId)
                .orElseThrow(() -> new IllegalArgumentException("Cuota no encontrada"));

        BigDecimal totalDiscount = fee.getDiscounts().stream()
                .map(MembershipFeeDiscount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        fee.setDiscountTotal(totalDiscount);
        fee.setFinalAmount(fee.getBaseAmount().subtract(totalDiscount));
        if (!fee.getDiscounts().isEmpty() && fee.getStatus() == MembershipFeeStatus.PENDING) {
            fee.setStatus(MembershipFeeStatus.CALCULATED);
        }
        fee.setUpdatedAt(LocalDateTime.now());

        return membershipFeeRepository.save(fee);
    }

    @Override
    public MembershipFee markFeeAsPaid(Long membershipFeeId, LocalDateTime paidAt) {
        MembershipFee fee = membershipFeeRepository.findById(membershipFeeId)
                .orElseThrow(() -> new IllegalArgumentException("Cuota no encontrada"));

        fee.setStatus(MembershipFeeStatus.PAID);
        fee.setPaidAt(paidAt != null ? paidAt : LocalDateTime.now());
        fee.setUpdatedAt(LocalDateTime.now());

        return membershipFeeRepository.save(fee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipFee> getFeesForUser(Long userId) {
        return membershipFeeRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipFee> getFeesByStatus(MembershipFeeStatus status) {
        return membershipFeeRepository.findByStatus(status);
    }
}
