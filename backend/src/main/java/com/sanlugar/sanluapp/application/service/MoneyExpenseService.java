package com.sanlugar.sanluapp.application.service;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.MoneyExpense;

public interface MoneyExpenseService {
    MoneyExpense create(MoneyExpense expense);
    MoneyExpense update(Long id, MoneyExpense expense);
    MoneyExpense approve(Long id, Long approvedBy);
    MoneyExpense linkToTransaction(Long id, Long transactionId);
    List<MoneyExpense> findByApproved(Boolean approved);
    Optional<MoneyExpense> findById(Long id);
}
