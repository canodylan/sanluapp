package com.sanlugar.sanluapp.adapters.out.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sanlugar.sanluapp.adapters.mappers.FriendGroupMapper;
import com.sanlugar.sanluapp.domain.model.FriendGroup;
import com.sanlugar.sanluapp.domain.port.FriendGroupRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class FriendGroupRepositoryAdapter implements FriendGroupRepository {

    private final SpringDataFriendGroupRepository friendGroupRepository;
    private final SpringDataUserRepository userRepository;

    @Override
    public FriendGroup save(FriendGroup group) {
        if (group.getCreatedAt() == null) {
            group.setCreatedAt(LocalDateTime.now());
        }
        FriendGroupEntity entity = FriendGroupMapper.toEntity(group, this::getUserReference);
        FriendGroupEntity saved = friendGroupRepository.save(entity);
        return FriendGroupMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FriendGroup> findById(Long id) {
        return friendGroupRepository.findById(id).map(FriendGroupMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendGroup> findAll() {
        return friendGroupRepository.findAll().stream()
                .map(FriendGroupMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendGroup> findByMemberId(Long userId) {
        return friendGroupRepository.findByMemberId(userId).stream()
                .map(FriendGroupMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendGroup> findByUserInChargeId(Long userId) {
        return friendGroupRepository.findByUserInCharge_Id(userId).stream()
                .map(FriendGroupMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        friendGroupRepository.deleteById(id);
    }

    private UserEntity getUserReference(Long userId) {
        return userId == null ? null : userRepository.getReferenceById(userId);
    }
}
