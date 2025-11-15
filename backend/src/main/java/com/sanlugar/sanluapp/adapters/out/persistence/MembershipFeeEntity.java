package com.sanlugar.sanluapp.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "membership_fees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipFeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity user;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "days_attending", nullable = false)
    private Integer daysAttending;

    @Column(name = "base_amount", nullable = false)
    private BigDecimal baseAmount;

    @Column(name = "discount_total", nullable = false)
    private BigDecimal discountTotal;

    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "membershipFee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MembershipFeeDayEntity> attendanceDays = new LinkedHashSet<>();

    @OneToMany(mappedBy = "membershipFee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MembershipFeeDiscountEntity> discounts = new LinkedHashSet<>();
}
