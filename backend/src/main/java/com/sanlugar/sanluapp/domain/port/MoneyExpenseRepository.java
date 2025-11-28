package com.sanlugar.sanluapp.domain.port;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.MoneyExpense;

public interface MoneyExpenseRepository {
    MoneyExpense save(MoneyExpense expense);
    Optional<MoneyExpense> findById(Long id);
    List<MoneyExpense> findAll();
    List<MoneyExpense> findByApproved(Boolean approved);
}
