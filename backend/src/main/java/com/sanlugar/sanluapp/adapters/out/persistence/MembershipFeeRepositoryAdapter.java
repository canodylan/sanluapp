package com.sanlugar.sanluapp.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sanlugar.sanluapp.adapters.mappers.MembershipFeeMapper;
import com.sanlugar.sanluapp.domain.model.MembershipFee;
import com.sanlugar.sanluapp.domain.model.MembershipFeeStatus;
import com.sanlugar.sanluapp.domain.port.MembershipFeeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class MembershipFeeRepositoryAdapter implements MembershipFeeRepository {

    private final SpringDataMembershipFeeRepository membershipFeeRepository;
    private final SpringDataUserRepository userRepository;

    @Override
    public MembershipFee save(MembershipFee membershipFee) {
        MembershipFeeEntity entity = MembershipFeeMapper.toEntity(membershipFee, userRepository::getReferenceById);
        MembershipFeeEntity saved = membershipFeeRepository.save(entity);
        return MembershipFeeMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MembershipFee> findById(Long id) {
        return membershipFeeRepository.findById(id).map(MembershipFeeMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipFee> findByUserId(Long userId) {
        return membershipFeeRepository.findByUser_Id(userId).stream()
                .map(MembershipFeeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipFee> findByStatus(MembershipFeeStatus status) {
        return membershipFeeRepository.findByStatus(status.name()).stream()
                .map(MembershipFeeMapper::toDomain)
                .collect(Collectors.toList());
    }
}
