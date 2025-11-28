package com.sanlugar.sanluapp.domain.port;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.MoneyTransaction;
import com.sanlugar.sanluapp.domain.model.MoneyTransactionFilter;

public interface MoneyTransactionRepository {
    MoneyTransaction save(MoneyTransaction transaction);
    Optional<MoneyTransaction> findById(Long id);
    List<MoneyTransaction> findByFilter(MoneyTransactionFilter filter);
}
