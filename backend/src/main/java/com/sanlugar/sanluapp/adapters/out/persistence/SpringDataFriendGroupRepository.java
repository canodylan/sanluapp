package com.sanlugar.sanluapp.adapters.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataFriendGroupRepository extends JpaRepository<FriendGroupEntity, Long> {

    @Query("SELECT g FROM FriendGroupEntity g JOIN g.members m WHERE m.id = :userId")
    List<FriendGroupEntity> findByMemberId(@Param("userId") Long userId);

    List<FriendGroupEntity> findByUserInCharge_Id(Long userId);
}
