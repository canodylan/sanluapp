package com.sanlugar.sanluapp.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataClubAccountRepository extends JpaRepository<ClubAccountEntity, Long> {
    boolean existsByNameIgnoreCase(String name);
}
