package com.sanlugar.sanluapp.adapters.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sanlugar.sanluapp.adapters.mappers.ClubAccountMapper;
import com.sanlugar.sanluapp.domain.model.ClubAccount;
import com.sanlugar.sanluapp.domain.port.ClubAccountRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class ClubAccountRepositoryAdapter implements ClubAccountRepository {

    private final SpringDataClubAccountRepository clubAccountRepository;

    @Override
    public ClubAccount save(ClubAccount account) {
        ClubAccountEntity entity = ClubAccountMapper.toEntity(account);
        ClubAccountEntity saved = clubAccountRepository.save(entity);
        return ClubAccountMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubAccount> findAll() {
        return clubAccountRepository.findAll().stream()
                .map(ClubAccountMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClubAccount> findById(Long id) {
        return clubAccountRepository.findById(id).map(ClubAccountMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        clubAccountRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String name) {
        return clubAccountRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public void clearPrimaryExcept(Long id) {
        clubAccountRepository.clearPrimaryExcept(id);
    }

    @Override
    public void markPrimary(Long id) {
        clubAccountRepository.markPrimary(id);
    }
}
