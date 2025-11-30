package com.sanlugar.sanluapp.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sanlugar.sanluapp.domain.model.ClubAccount;
import com.sanlugar.sanluapp.domain.model.MoneyCategory;
import com.sanlugar.sanluapp.domain.model.MoneyExpense;
import com.sanlugar.sanluapp.domain.model.MoneyTransaction;
import com.sanlugar.sanluapp.domain.model.MoneyTransactionType;
import com.sanlugar.sanluapp.domain.port.MoneyExpenseRepository;
import com.sanlugar.sanluapp.domain.port.ClubAccountRepository;
import com.sanlugar.sanluapp.domain.port.MoneyCategoryRepository;
import com.sanlugar.sanluapp.domain.port.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultMoneyExpenseService implements MoneyExpenseService {

    private final MoneyExpenseRepository moneyExpenseRepository;
    private final UserRepository userRepository;
    private final ClubAccountRepository clubAccountRepository;
    private final MoneyCategoryRepository moneyCategoryRepository;
    private final MoneyTransactionService moneyTransactionService;

    @Override
    public MoneyExpense create(MoneyExpense expense) {
        validateForCreate(expense);
        userRepository.findById(expense.getRequestedBy())
                .orElseThrow(() -> new IllegalArgumentException("Usuario solicitante no encontrado"));
        expense.setCategory(resolveCategory(expense.getCategoryId()));
        expense.setAccount(resolveAccount(expense.getAccountId()));
        expense.setId(null);
        expense.setApproved(Boolean.FALSE);
        expense.setApprovedBy(null);
        expense.setApprovedAt(null);
        expense.setCreatedAt(LocalDateTime.now());
        return moneyExpenseRepository.save(expense);
    }

    @Override
    public MoneyExpense update(Long id, MoneyExpense expense) {
        MoneyExpense existing = moneyExpenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud de gasto no encontrada"));
        if (expense.getDescription() != null && StringUtils.hasText(expense.getDescription())) {
            existing.setDescription(expense.getDescription());
        }
        if (expense.getAmount() != null) {
            ensurePositive(expense.getAmount());
            existing.setAmount(expense.getAmount());
        }
        if (expense.getReceiptUrl() != null) {
            existing.setReceiptUrl(expense.getReceiptUrl());
        }
        if (expense.getCategoryId() != null) {
            existing.setCategory(resolveCategory(expense.getCategoryId()));
            existing.setCategoryId(expense.getCategoryId());
        }
        if (expense.getAccountId() != null) {
            ClubAccount account = resolveAccount(expense.getAccountId());
            if (account == null) {
                throw new IllegalArgumentException("La cuenta indicada no existe");
            }
            existing.setAccount(account);
            existing.setAccountId(expense.getAccountId());
        }
        return moneyExpenseRepository.save(existing);
    }

    @Override
    public MoneyExpense updateAssignment(Long id, Long categoryId, Long accountId) {
        MoneyExpense existing = moneyExpenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud de gasto no encontrada"));
        existing.setCategoryId(categoryId);
        existing.setCategory(resolveCategory(categoryId));
        existing.setAccountId(accountId);
        existing.setAccount(resolveAccount(accountId));
        return moneyExpenseRepository.save(existing);
    }

    @Override
    public MoneyExpense approve(Long id, Long approvedBy, Long categoryId, Long accountId) {
        MoneyExpense expense = moneyExpenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud de gasto no encontrada"));
        if (Boolean.TRUE.equals(expense.getApproved())) {
            throw new IllegalStateException("El gasto ya fue aprobado");
        }
        if (approvedBy == null) {
            throw new IllegalArgumentException("Debe indicar el usuario aprobador");
        }
        userRepository.findById(approvedBy)
                .orElseThrow(() -> new IllegalArgumentException("Usuario aprobador no encontrado"));

        Long resolvedCategoryId = categoryId != null ? categoryId : expense.getCategoryId();
        Long resolvedAccountId = accountId != null ? accountId : expense.getAccountId();
        ClubAccount account = resolveAccount(resolvedAccountId);
        if (account == null) {
            throw new IllegalStateException("Debe indicar la cuenta a la que se imputará el gasto");
        }
        expense.setCategoryId(resolvedCategoryId);
        expense.setCategory(resolveCategory(resolvedCategoryId));
        expense.setAccountId(account.getId());
        expense.setAccount(account);
        expense.setApprovedBy(approvedBy);
        moneyExpenseRepository.save(expense);

        MoneyTransaction transaction = MoneyTransaction.builder()
                .type(MoneyTransactionType.EXPENSE)
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .categoryId(resolvedCategoryId)
                .accountFromId(account.getId())
                .createdBy(approvedBy)
                .relatedEntityId(expense.getId())
                .relatedEntityType("money_expense")
                .build();

        moneyTransactionService.recordTransaction(transaction);
        return moneyExpenseRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("No se pudo recuperar el gasto aprobado"));
    }

    @Override
    public MoneyExpense linkToTransaction(Long id, Long transactionId) {
        if (transactionId == null) {
            throw new IllegalArgumentException("Se requiere el identificador de la transacción");
        }
        MoneyExpense expense = moneyExpenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud de gasto no encontrada"));
        if (expense.getTransactionId() != null && !expense.getTransactionId().equals(transactionId)) {
            throw new IllegalStateException("El gasto ya está vinculado a otra transacción");
        }
        expense.setTransactionId(transactionId);
        if (!Boolean.TRUE.equals(expense.getApproved())) {
            expense.setApproved(Boolean.TRUE);
            expense.setApprovedAt(LocalDateTime.now());
        }
        return moneyExpenseRepository.save(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoneyExpense> findByApproved(Boolean approved) {
        if (approved == null) {
            return moneyExpenseRepository.findAll();
        }
        return moneyExpenseRepository.findByApproved(approved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MoneyExpense> findById(Long id) {
        return moneyExpenseRepository.findById(id);
    }

    private void validateForCreate(MoneyExpense expense) {
        if (expense == null || !StringUtils.hasText(expense.getDescription())) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
        if (expense.getRequestedBy() == null) {
            throw new IllegalArgumentException("Debe indicar el usuario solicitante");
        }
        ensurePositive(expense.getAmount());
    }

    private void ensurePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
    }

    private MoneyCategory resolveCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return moneyCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
    }

    private ClubAccount resolveAccount(Long accountId) {
        if (accountId == null) {
            return null;
        }
        return clubAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta contable no encontrada"));
    }
}
