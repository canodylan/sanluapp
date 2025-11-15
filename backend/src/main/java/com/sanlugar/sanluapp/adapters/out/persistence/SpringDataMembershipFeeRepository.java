package com.sanlugar.sanluapp.adapters.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataMembershipFeeRepository extends JpaRepository<MembershipFeeEntity, Long> {

    @EntityGraph(attributePaths = {"attendanceDays", "discounts"})
    List<MembershipFeeEntity> findByUser_Id(Long userId);

    @EntityGraph(attributePaths = {"attendanceDays", "discounts"})
    List<MembershipFeeEntity> findByStatus(String status);
}
