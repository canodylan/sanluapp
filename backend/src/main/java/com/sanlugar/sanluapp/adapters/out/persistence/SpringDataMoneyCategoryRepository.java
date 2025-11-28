package com.sanlugar.sanluapp.adapters.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataMoneyCategoryRepository extends JpaRepository<MoneyCategoryEntity, Long> {
    Optional<MoneyCategoryEntity> findByNameIgnoreCase(String name);
}
