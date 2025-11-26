package com.sanlugar.sanluapp.adapters.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import com.sanlugar.sanluapp.adapters.out.persistence.MembershipFeeDayEntity;
import com.sanlugar.sanluapp.adapters.out.persistence.MembershipFeeDiscountEntity;
import com.sanlugar.sanluapp.adapters.out.persistence.MembershipFeeEntity;
import com.sanlugar.sanluapp.adapters.out.persistence.UserEntity;
import com.sanlugar.sanluapp.domain.model.MembershipFee;
import com.sanlugar.sanluapp.domain.model.MembershipFeeDay;
import com.sanlugar.sanluapp.domain.model.MembershipFeeDiscount;
import com.sanlugar.sanluapp.domain.model.MembershipFeeStatus;
import com.sanlugar.sanluapp.domain.model.MembershipFeeUser;

public final class MembershipFeeMapper {

    private MembershipFeeMapper() {}

    public static MembershipFee toDomain(MembershipFeeEntity entity) {
        if (entity == null) {
            return null;
        }
        return MembershipFee.builder()
                .id(entity.getId())
                .userId(entity.getUser() == null ? null : entity.getUser().getId())
                .year(entity.getYear())
                .daysAttending(entity.getDaysAttending())
                .baseAmount(entity.getBaseAmount())
                .discountTotal(entity.getDiscountTotal())
                .finalAmount(entity.getFinalAmount())
                .status(MembershipFeeStatus.valueOf(entity.getStatus()))
                .paidAt(entity.getPaidAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .user(toUser(entity.getUser()))
                .attendanceDays((entity.getAttendanceDays() == null ? java.util.stream.Stream.<MembershipFeeDayEntity>empty()
                        : entity.getAttendanceDays().stream())
                        .map(day -> MembershipFeeDay.builder()
                                .id(day.getId())
                                .attendanceDate(day.getAttendanceDate())
                                .build())
                        .collect(Collectors.toCollection(java.util.LinkedHashSet::new)))
                .discounts((entity.getDiscounts() == null ? java.util.stream.Stream.<MembershipFeeDiscountEntity>empty()
                        : entity.getDiscounts().stream())
                        .map(discount -> MembershipFeeDiscount.builder()
                                .id(discount.getId())
                                .membershipFeeId(entity.getId())
                                .concept(discount.getConcept())
                                .amount(discount.getAmount())
                                .createdAt(discount.getCreatedAt())
                                .build())
                        .collect(Collectors.toCollection(java.util.LinkedHashSet::new)))
                .build();
    }

    public static MembershipFeeEntity toEntity(MembershipFee domain, java.util.function.Function<Long, UserEntity> userResolver) {
        if (domain == null) {
            return null;
        }
        MembershipFeeEntity entity = MembershipFeeEntity.builder()
                .id(domain.getId())
                .year(domain.getYear())
                .daysAttending(domain.getDaysAttending())
                .baseAmount(domain.getBaseAmount())
                .discountTotal(domain.getDiscountTotal())
                .finalAmount(domain.getFinalAmount())
                .status(domain.getStatus().name())
                .paidAt(domain.getPaidAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();

        if (domain.getUserId() != null) {
            entity.setUser(userResolver.apply(domain.getUserId()));
        }

        java.util.stream.Stream<MembershipFeeDay> dayStream = domain.getAttendanceDays() == null
                ? java.util.stream.Stream.empty()
                : domain.getAttendanceDays().stream();
        Set<MembershipFeeDayEntity> dayEntities = dayStream
                .map(day -> MembershipFeeDayEntity.builder()
                        .id(day.getId())
                        .attendanceDate(day.getAttendanceDate())
                        .membershipFee(entity)
                        .build())
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
        entity.setAttendanceDays(dayEntities);

        java.util.stream.Stream<MembershipFeeDiscount> discountStream = domain.getDiscounts() == null
                ? java.util.stream.Stream.empty()
                : domain.getDiscounts().stream();
        Set<MembershipFeeDiscountEntity> discountEntities = discountStream
                .map(discount -> MembershipFeeDiscountEntity.builder()
                        .id(discount.getId())
                        .concept(discount.getConcept())
                        .amount(discount.getAmount())
                        .createdAt(discount.getCreatedAt())
                        .membershipFee(entity)
                        .build())
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
        entity.setDiscounts(discountEntities);

        return entity;
    }

        private static MembershipFeeUser toUser(UserEntity userEntity) {
                if (userEntity == null) {
                        return null;
                }
                return MembershipFeeUser.builder()
                                .id(userEntity.getId())
                                .username(userEntity.getUsername())
                                .nickname(userEntity.getNickname())
                                .firstName(userEntity.getFirstName())
                                .lastName(userEntity.getLastName())
                                .build();
        }
}
