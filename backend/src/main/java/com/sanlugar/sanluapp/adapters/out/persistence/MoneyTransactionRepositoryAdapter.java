package com.sanlugar.sanluapp.adapters.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sanlugar.sanluapp.adapters.mappers.MoneyTransactionMapper;
import com.sanlugar.sanluapp.domain.model.MoneyTransaction;
import com.sanlugar.sanluapp.domain.model.MoneyTransactionFilter;
import com.sanlugar.sanluapp.domain.port.MoneyTransactionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class MoneyTransactionRepositoryAdapter implements MoneyTransactionRepository {

    private final SpringDataMoneyTransactionRepository transactionRepository;
    private final SpringDataClubAccountRepository clubAccountRepository;
    private final SpringDataMoneyCategoryRepository categoryRepository;
    private final SpringDataUserRepository userRepository;

    @Override
    public MoneyTransaction save(MoneyTransaction transaction) {
        MoneyTransactionEntity entity = MoneyTransactionMapper.toEntity(
                transaction,
                clubAccountRepository::getReferenceById,
                categoryRepository::getReferenceById,
                userRepository::getReferenceById);
        MoneyTransactionEntity saved = transactionRepository.save(entity);
        return MoneyTransactionMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MoneyTransaction> findById(Long id) {
        return transactionRepository.findById(id).map(MoneyTransactionMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoneyTransaction> findByFilter(MoneyTransactionFilter filter) {
        var criteria = filter == null ? MoneyTransactionFilter.builder().build() : filter;
        return transactionRepository.search(
                criteria.getType(),
                criteria.getCategoryId(),
                criteria.getFromDate(),
                criteria.getToDate(),
                criteria.getAccountId(),
                criteria.getCreatedBy())
                .stream()
                .map(MoneyTransactionMapper::toDomain)
                .toList();
    }
}
