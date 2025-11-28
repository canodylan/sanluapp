package com.sanlugar.sanluapp.domain.port;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.ClubAccount;

public interface ClubAccountRepository {
    ClubAccount save(ClubAccount account);
    List<ClubAccount> findAll();
    Optional<ClubAccount> findById(Long id);
    void deleteById(Long id);
    boolean existsByNameIgnoreCase(String name);
}
