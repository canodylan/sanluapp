package com.sanlugar.sanluapp.application.service;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.ClubAccount;

public interface ClubAccountService {
    ClubAccount create(ClubAccount account);
    ClubAccount update(Long id, ClubAccount account);
    ClubAccount setPrimary(Long id);
    List<ClubAccount> findAll();
    Optional<ClubAccount> findById(Long id);
    void delete(Long id);
}
