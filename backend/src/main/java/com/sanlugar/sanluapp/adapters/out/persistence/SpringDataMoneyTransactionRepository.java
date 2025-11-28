package com.sanlugar.sanluapp.adapters.out.persistence;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanlugar.sanluapp.domain.model.MoneyTransactionType;

@Repository
public interface SpringDataMoneyTransactionRepository extends JpaRepository<MoneyTransactionEntity, Long> {

    @Query("""
        SELECT DISTINCT t FROM MoneyTransactionEntity t
        LEFT JOIN FETCH t.category
        LEFT JOIN FETCH t.accountFrom
        LEFT JOIN FETCH t.accountTo
        LEFT JOIN FETCH t.createdBy
        WHERE (:type IS NULL OR t.type = :type)
          AND (:categoryId IS NULL OR t.category.id = :categoryId)
          AND (:fromDate IS NULL OR t.transactionDate >= :fromDate)
          AND (:toDate IS NULL OR t.transactionDate <= :toDate)
          AND (:accountId IS NULL OR t.accountFrom.id = :accountId OR t.accountTo.id = :accountId)
          AND (:createdBy IS NULL OR t.createdBy.id = :createdBy)
        ORDER BY t.transactionDate DESC, t.createdAt DESC
        """)
    List<MoneyTransactionEntity> search(
        @Param("type") MoneyTransactionType type,
        @Param("categoryId") Long categoryId,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate,
        @Param("accountId") Long accountId,
        @Param("createdBy") Long createdBy
    );
}
