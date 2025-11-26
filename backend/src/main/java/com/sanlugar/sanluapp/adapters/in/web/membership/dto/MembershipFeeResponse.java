package com.sanlugar.sanluapp.adapters.in.web.membership.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.sanlugar.sanluapp.domain.model.MembershipFee;
import com.sanlugar.sanluapp.domain.model.MembershipFeeDay;
import com.sanlugar.sanluapp.domain.model.MembershipFeeDiscount;
import com.sanlugar.sanluapp.domain.model.MembershipFeeUser;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MembershipFeeResponse {
    private Long id;
    private Long userId;
    private Integer year;
    private Integer daysAttending;
    private BigDecimal baseAmount;
    private BigDecimal discountTotal;
    private BigDecimal finalAmount;
    private String status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AttendanceDayDto> attendanceDays;
    private List<DiscountDto> discounts;
    private UserSummaryDto user;

    public static MembershipFeeResponse from(MembershipFee fee) {
        return MembershipFeeResponse.builder()
                .id(fee.getId())
                .userId(fee.getUserId())
                .year(fee.getYear())
                .daysAttending(fee.getDaysAttending())
                .baseAmount(fee.getBaseAmount())
                .discountTotal(fee.getDiscountTotal())
                .finalAmount(fee.getFinalAmount())
                .status(fee.getStatus().name())
                .paidAt(fee.getPaidAt())
                .createdAt(fee.getCreatedAt())
                .updatedAt(fee.getUpdatedAt())
                .attendanceDays(fee.getAttendanceDays().stream()
                        .map(AttendanceDayDto::from)
                        .toList())
                .discounts(fee.getDiscounts().stream()
                        .map(DiscountDto::from)
                        .toList())
                .user(UserSummaryDto.from(fee.getUser(), fee.getUserId()))
                .build();
    }

    @Data
    @Builder
    public static class AttendanceDayDto {
        private Long id;
        private LocalDate attendanceDate;

        public static AttendanceDayDto from(MembershipFeeDay day) {
            return AttendanceDayDto.builder()
                    .id(day.getId())
                    .attendanceDate(day.getAttendanceDate())
                    .build();
        }
    }

    @Data
    @Builder
    public static class DiscountDto {
        private Long id;
        private String concept;
        private BigDecimal amount;
        private LocalDateTime createdAt;

        public static DiscountDto from(MembershipFeeDiscount discount) {
            return DiscountDto.builder()
                    .id(discount.getId())
                    .concept(discount.getConcept())
                    .amount(discount.getAmount())
                    .createdAt(discount.getCreatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    public static class UserSummaryDto {
        private Long id;
        private String username;
        private String nickname;
        private String firstName;
        private String lastName;
        private String displayName;

        public static UserSummaryDto from(MembershipFeeUser user, Long fallbackId) {
            if (user == null) {
                return UserSummaryDto.builder()
                        .id(fallbackId)
                        .displayName("Miembro " + (fallbackId != null ? ("#" + fallbackId) : "desconocido"))
                        .build();
            }
            return UserSummaryDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .displayName(user.getDisplayName())
                    .build();
        }
    }
}
