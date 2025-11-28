package com.sanlugar.sanluapp.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanlugar.sanluapp.domain.model.ClubAccount;
import com.sanlugar.sanluapp.domain.model.MoneyExpense;
import com.sanlugar.sanluapp.domain.model.MoneyTransaction;
import com.sanlugar.sanluapp.domain.model.MoneyTransactionFilter;
import com.sanlugar.sanluapp.domain.model.MoneyTransactionType;
import com.sanlugar.sanluapp.domain.port.ClubAccountRepository;
import com.sanlugar.sanluapp.domain.port.MoneyCategoryRepository;
import com.sanlugar.sanluapp.domain.port.MoneyExpenseRepository;
import com.sanlugar.sanluapp.domain.port.MoneyTransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultMoneyTransactionService implements MoneyTransactionService {

    private static final String MONEY_EXPENSE_RELATED_TYPE = "money_expense";

    private final MoneyTransactionRepository moneyTransactionRepository;
    private final ClubAccountRepository clubAccountRepository;
    private final MoneyCategoryRepository moneyCategoryRepository;
    private final MoneyExpenseRepository moneyExpenseRepository;

    @Override
    public MoneyTransaction recordTransaction(MoneyTransaction transaction) {
        validateTransaction(transaction);

        ClubAccount accountFrom = transaction.getAccountFromId() == null
                ? null
                : clubAccountRepository.findById(transaction.getAccountFromId())
                        .orElseThrow(() -> new IllegalArgumentException("Cuenta origen no encontrada"));
        ClubAccount accountTo = transaction.getAccountToId() == null
                ? null
                : clubAccountRepository.findById(transaction.getAccountToId())
                        .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no encontrada"));

        if (transaction.getCategoryId() != null) {
            moneyCategoryRepository.findById(transaction.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        }

        applyBalanceChanges(transaction, accountFrom, accountTo);

        if (transaction.getCreatedAt() == null) {
            transaction.setCreatedAt(LocalDateTime.now());
        }
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDate.now());
        }
        if (transaction.getRelatedEntityId() != null && transaction.getRelatedEntityType() == null) {
            transaction.setRelatedEntityType(MONEY_EXPENSE_RELATED_TYPE);
        }

        MoneyTransaction saved = moneyTransactionRepository.save(transaction);
        handleExpenseLink(saved);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoneyTransaction> findByFilter(MoneyTransactionFilter filter) {
        return moneyTransactionRepository.findByFilter(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MoneyTransaction> findById(Long id) {
        return moneyTransactionRepository.findById(id);
    }

    private void validateTransaction(MoneyTransaction transaction) {
        if (transaction == null || transaction.getType() == null) {
            throw new IllegalArgumentException("El tipo de transacción es obligatorio");
        }
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        MoneyTransactionType type = transaction.getType();
        switch (type) {
            case INCOME -> {
                if (transaction.getAccountToId() == null) {
                    throw new IllegalArgumentException("Una entrada requiere cuenta destino");
                }
                if (transaction.getAccountFromId() != null) {
                    throw new IllegalArgumentException("Una entrada no puede tener cuenta origen");
                }
            }
            case EXPENSE -> {
                if (transaction.getAccountFromId() == null) {
                    throw new IllegalArgumentException("Un gasto requiere cuenta origen");
                }
                if (transaction.getAccountToId() != null) {
                    throw new IllegalArgumentException("Un gasto no puede tener cuenta destino");
                }
            }
            case TRANSFER -> {
                if (transaction.getAccountFromId() == null || transaction.getAccountToId() == null) {
                    throw new IllegalArgumentException("Una transferencia requiere cuentas origen y destino");
                }
                if (transaction.getAccountFromId().equals(transaction.getAccountToId())) {
                    throw new IllegalArgumentException("La transferencia debe usar cuentas distintas");
                }
            }
        }
        if (transaction.getRelatedEntityId() != null
                && transaction.getRelatedEntityType() != null
                && !MONEY_EXPENSE_RELATED_TYPE.equals(transaction.getRelatedEntityType())) {
            throw new IllegalArgumentException("Solo se admite enlazar con solicitudes de gasto");
        }
        if (transaction.getRelatedEntityId() != null && transaction.getType() == MoneyTransactionType.INCOME) {
            throw new IllegalArgumentException("No se puede asociar un ingreso a una solicitud de gasto");
        }
    }

    private void applyBalanceChanges(MoneyTransaction transaction, ClubAccount from, ClubAccount to) {
        BigDecimal amount = transaction.getAmount();
        switch (transaction.getType()) {
            case INCOME -> {
                to.setCurrentBalance(to.getCurrentBalance().add(amount));
                clubAccountRepository.save(to);
            }
            case EXPENSE -> {
                ensureSufficientFunds(from, amount);
                from.setCurrentBalance(from.getCurrentBalance().subtract(amount));
                clubAccountRepository.save(from);
            }
            case TRANSFER -> {
                ensureSufficientFunds(from, amount);
                from.setCurrentBalance(from.getCurrentBalance().subtract(amount));
                to.setCurrentBalance(to.getCurrentBalance().add(amount));
                clubAccountRepository.save(from);
                clubAccountRepository.save(to);
            }
        }
    }

    private void ensureSufficientFunds(ClubAccount account, BigDecimal amount) {
        if (account == null) {
            throw new IllegalArgumentException("La cuenta especificada no existe");
        }
        if (account.getCurrentBalance() == null) {
            account.setCurrentBalance(BigDecimal.ZERO);
        }
        if (account.getCurrentBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Fondos insuficientes en la cuenta " + account.getName());
        }
    }

    private void handleExpenseLink(MoneyTransaction saved) {
        if (saved.getRelatedEntityId() == null || saved.getRelatedEntityType() == null) {
            return;
        }
        if (!MONEY_EXPENSE_RELATED_TYPE.equals(saved.getRelatedEntityType())) {
            return;
        }
        MoneyExpense expense = moneyExpenseRepository.findById(saved.getRelatedEntityId())
                .orElseThrow(() -> new IllegalArgumentException("Solicitud de gasto no encontrada"));
        if (Boolean.TRUE.equals(expense.getApproved()) && expense.getTransactionId() != null) {
            throw new IllegalStateException("El gasto ya fue aprobado y vinculado a otra transacción");
        }
        expense.setTransactionId(saved.getId());
        expense.setApproved(Boolean.TRUE);
        expense.setApprovedAt(LocalDateTime.now());
        expense.setApprovedBy(saved.getCreatedBy());
        moneyExpenseRepository.save(expense);
    }
}
