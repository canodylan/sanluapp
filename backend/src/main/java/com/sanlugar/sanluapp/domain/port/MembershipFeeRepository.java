package com.sanlugar.sanluapp.domain.port;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.MembershipFee;
import com.sanlugar.sanluapp.domain.model.MembershipFeeStatus;

public interface MembershipFeeRepository {
    MembershipFee save(MembershipFee membershipFee);
    Optional<MembershipFee> findById(Long id);
    List<MembershipFee> findByUserId(Long userId);
    List<MembershipFee> findByStatus(MembershipFeeStatus status);
}
