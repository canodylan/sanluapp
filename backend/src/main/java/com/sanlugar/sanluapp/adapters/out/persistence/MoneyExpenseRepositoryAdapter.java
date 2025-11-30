package com.sanlugar.sanluapp.adapters.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sanlugar.sanluapp.adapters.mappers.MoneyExpenseMapper;
import com.sanlugar.sanluapp.domain.model.MoneyExpense;
import com.sanlugar.sanluapp.domain.port.MoneyExpenseRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class MoneyExpenseRepositoryAdapter implements MoneyExpenseRepository {

    private final SpringDataMoneyExpenseRepository expenseRepository;
    private final SpringDataUserRepository userRepository;
    private final SpringDataMoneyTransactionRepository transactionRepository;
    private final SpringDataMoneyCategoryRepository categoryRepository;
    private final SpringDataClubAccountRepository clubAccountRepository;

    @Override
    public MoneyExpense save(MoneyExpense expense) {
        MoneyExpenseEntity entity = MoneyExpenseMapper.toEntity(
                expense,
                userRepository::getReferenceById,
            transactionRepository::getReferenceById,
            categoryRepository::getReferenceById,
            clubAccountRepository::getReferenceById);
        MoneyExpenseEntity saved = expenseRepository.save(entity);
        return MoneyExpenseMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MoneyExpense> findById(Long id) {
        return expenseRepository.findDetailedById(id).map(MoneyExpenseMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoneyExpense> findAll() {
        return expenseRepository.findAll().stream()
                .map(MoneyExpenseMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoneyExpense> findByApproved(Boolean approved) {
        return expenseRepository.findByApproved(approved).stream()
                .map(MoneyExpenseMapper::toDomain)
                .toList();
    }
}
