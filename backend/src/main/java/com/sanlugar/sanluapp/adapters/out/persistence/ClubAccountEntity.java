package com.sanlugar.sanluapp.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "club_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "current_balance", precision = 10, scale = 2, nullable = false)
    private BigDecimal currentBalance;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_primary", nullable = false)
    private Boolean primaryAccount;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }
        if (primaryAccount == null) {
            primaryAccount = Boolean.FALSE;
        }
    }
}
