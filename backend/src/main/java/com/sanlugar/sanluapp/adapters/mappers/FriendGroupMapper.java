package com.sanlugar.sanluapp.adapters.mappers;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sanlugar.sanluapp.adapters.out.persistence.FriendGroupEntity;
import com.sanlugar.sanluapp.adapters.out.persistence.UserEntity;
import com.sanlugar.sanluapp.domain.model.FriendGroup;

public final class FriendGroupMapper {

    private FriendGroupMapper() {}

    public static FriendGroup toDomain(FriendGroupEntity entity) {
        if (entity == null) {
            return null;
        }
        return FriendGroup.builder()
                .id(entity.getId())
                .name(entity.getName())
                .userInChargeId(entity.getUserInCharge() == null ? null : entity.getUserInCharge().getId())
                .createdAt(entity.getCreatedAt())
                .memberIds(entity.getMembers() == null
                        ? Collections.emptySet()
                        : entity.getMembers().stream().map(UserEntity::getId).collect(Collectors.toSet()))
                .build();
    }

    public static FriendGroupEntity toEntity(FriendGroup group,
                                             Function<Long, UserEntity> userResolver) {
        if (group == null) {
            return null;
        }
        FriendGroupEntity entity = FriendGroupEntity.builder()
                .id(group.getId())
                .name(group.getName())
                .createdAt(group.getCreatedAt())
                .build();

        if (group.getUserInChargeId() != null) {
            entity.setUserInCharge(userResolver.apply(group.getUserInChargeId()));
        }

        Set<Long> memberIds = group.getMemberIds();
        if (memberIds != null && !memberIds.isEmpty()) {
            entity.setMembers(memberIds.stream()
                    .map(userResolver)
                    .collect(Collectors.toSet()));
        }

        return entity;
    }
}
