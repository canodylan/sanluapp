package com.sanlugar.sanluapp.application.service;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.MoneyTransaction;
import com.sanlugar.sanluapp.domain.model.MoneyTransactionFilter;

public interface MoneyTransactionService {
    MoneyTransaction recordTransaction(MoneyTransaction transaction);
    List<MoneyTransaction> findByFilter(MoneyTransactionFilter filter);
    Optional<MoneyTransaction> findById(Long id);
}
