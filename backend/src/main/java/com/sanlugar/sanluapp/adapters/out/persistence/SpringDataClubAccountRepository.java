package com.sanlugar.sanluapp.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataClubAccountRepository extends JpaRepository<ClubAccountEntity, Long> {
    boolean existsByNameIgnoreCase(String name);

    @Modifying(clearAutomatically = true)
    @Query("update ClubAccountEntity c set c.primaryAccount = false where c.primaryAccount = true and c.id <> :id")
    void clearPrimaryExcept(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("update ClubAccountEntity c set c.primaryAccount = true where c.id = :id")
    void markPrimary(@Param("id") Long id);
}
