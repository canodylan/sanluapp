package com.sanlugar.sanluapp.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sanlugar.sanluapp.domain.model.ClubAccount;
import com.sanlugar.sanluapp.domain.port.ClubAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultClubAccountService implements ClubAccountService {

    private final ClubAccountRepository clubAccountRepository;

    @Override
    public ClubAccount create(ClubAccount account) {
        validate(account);
        if (clubAccountRepository.existsByNameIgnoreCase(account.getName())) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese nombre");
        }
        account.setId(null);
        account.setCreatedAt(account.getCreatedAt() == null ? LocalDateTime.now() : account.getCreatedAt());
        account.setCurrentBalance(account.getCurrentBalance() == null ? BigDecimal.ZERO : account.getCurrentBalance());
        boolean requestedPrimary = Boolean.TRUE.equals(account.getPrimary());
        ClubAccount saved = clubAccountRepository.save(account);
        if (requestedPrimary || !existsPrimary()) {
            return setPrimary(saved.getId());
        }
        return saved;
    }

    @Override
    public ClubAccount update(Long id, ClubAccount account) {
        validate(account);
        ClubAccount existing = clubAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        boolean changingName = !existing.getName().equalsIgnoreCase(account.getName());
        if (changingName && clubAccountRepository.existsByNameIgnoreCase(account.getName())) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese nombre");
        }

        existing.setName(account.getName());
        existing.setDescription(account.getDescription());
        if (account.getCurrentBalance() != null) {
            existing.setCurrentBalance(account.getCurrentBalance());
        }
        ClubAccount updated = clubAccountRepository.save(existing);
        if (Boolean.TRUE.equals(account.getPrimary())) {
            return setPrimary(updated.getId());
        }
        return updated;
    }

    @Override
    public ClubAccount setPrimary(Long id) {
        ClubAccount target = clubAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        clubAccountRepository.clearPrimaryExcept(id);
        clubAccountRepository.markPrimary(id);

        target.setPrimary(Boolean.TRUE);
        return target;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubAccount> findAll() {
        return clubAccountRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClubAccount> findById(Long id) {
        return clubAccountRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        clubAccountRepository.deleteById(id);
    }

    private void validate(ClubAccount account) {
        if (account == null || !StringUtils.hasText(account.getName())) {
            throw new IllegalArgumentException("El nombre de la cuenta es obligatorio");
        }
    }

    private boolean existsPrimary() {
        return clubAccountRepository.findAll().stream()
                .anyMatch(account -> Boolean.TRUE.equals(account.getPrimary()));
    }
}
