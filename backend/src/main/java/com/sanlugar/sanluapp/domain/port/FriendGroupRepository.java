package com.sanlugar.sanluapp.domain.port;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.FriendGroup;

public interface FriendGroupRepository {
    FriendGroup save(FriendGroup group);
    Optional<FriendGroup> findById(Long id);
    List<FriendGroup> findAll();
    List<FriendGroup> findByMemberId(Long userId);
    List<FriendGroup> findByUserInChargeId(Long userId);
    void deleteById(Long id);
}
