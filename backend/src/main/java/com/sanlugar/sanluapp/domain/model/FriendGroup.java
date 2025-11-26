package com.sanlugar.sanluapp.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain aggregate representing a group of friends and its members.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendGroup {
    private Long id;
    private String name;
    private Long userInChargeId;
    private LocalDateTime createdAt;
    @Builder.Default
    private Set<Long> memberIds = new HashSet<>();
}
