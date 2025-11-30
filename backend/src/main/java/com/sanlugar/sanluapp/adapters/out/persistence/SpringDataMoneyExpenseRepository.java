package com.sanlugar.sanluapp.adapters.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataMoneyExpenseRepository extends JpaRepository<MoneyExpenseEntity, Long> {

    @Override
    @EntityGraph(attributePaths = {
        "requestedBy",
        "approvedBy",
        "category",
        "account",
        "transaction",
        "transaction.category",
        "transaction.accountFrom",
        "transaction.accountTo"
    })
    List<MoneyExpenseEntity> findAll();

    @EntityGraph(attributePaths = {
        "requestedBy",
        "approvedBy",
        "category",
        "account",
        "transaction",
        "transaction.category",
        "transaction.accountFrom",
        "transaction.accountTo"
    })
    List<MoneyExpenseEntity> findByApproved(Boolean approved);

    @EntityGraph(attributePaths = {
        "requestedBy",
        "approvedBy",
        "category",
        "account",
        "transaction",
        "transaction.category",
        "transaction.accountFrom",
        "transaction.accountTo"
    })
    Optional<MoneyExpenseEntity> findDetailedById(Long id);
}
