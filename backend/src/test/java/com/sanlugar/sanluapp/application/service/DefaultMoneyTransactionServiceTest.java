package com.sanlugar.sanluapp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sanlugar.sanluapp.domain.model.ClubAccount;
import com.sanlugar.sanluapp.domain.model.MoneyExpense;
import com.sanlugar.sanluapp.domain.model.MoneyTransaction;
import com.sanlugar.sanluapp.domain.model.MoneyTransactionType;
import com.sanlugar.sanluapp.domain.port.ClubAccountRepository;
import com.sanlugar.sanluapp.domain.port.MoneyCategoryRepository;
import com.sanlugar.sanluapp.domain.port.MoneyExpenseRepository;
import com.sanlugar.sanluapp.domain.port.MoneyTransactionRepository;

@ExtendWith(MockitoExtension.class)
class DefaultMoneyTransactionServiceTest {

    @Mock
    private MoneyTransactionRepository moneyTransactionRepository;
    @Mock
    private ClubAccountRepository clubAccountRepository;
    @Mock
    private MoneyCategoryRepository moneyCategoryRepository;
    @Mock
    private MoneyExpenseRepository moneyExpenseRepository;

    private DefaultMoneyTransactionService service;

    @BeforeEach
    void setUp() {
        service = new DefaultMoneyTransactionService(moneyTransactionRepository, clubAccountRepository,
                moneyCategoryRepository, moneyExpenseRepository);
    }

    @Test
    void recordTransaction_incomeUpdatesDestinationBalance() {
        ClubAccount destination = ClubAccount.builder()
                .id(1L)
                .name("Caja Principal")
                .currentBalance(new BigDecimal("100.00"))
                .build();

        MoneyTransaction request = MoneyTransaction.builder()
                .type(MoneyTransactionType.INCOME)
                .amount(new BigDecimal("50.00"))
                .accountToId(1L)
                .createdBy(10L)
                .build();

        when(clubAccountRepository.findById(1L)).thenReturn(Optional.of(destination));
        when(moneyTransactionRepository.save(any())).thenAnswer(invocation -> {
            MoneyTransaction tx = invocation.getArgument(0);
            tx.setId(99L);
            tx.setAccountTo(destination);
            return tx;
        });

        MoneyTransaction result = service.recordTransaction(request);

        assertThat(result.getId()).isEqualTo(99L);
        ArgumentCaptor<ClubAccount> accountCaptor = ArgumentCaptor.forClass(ClubAccount.class);
        verify(clubAccountRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue().getCurrentBalance()).isEqualByComparingTo("150.00");
    }

    @Test
    void recordTransaction_linksExpenseWhenProvided() {
        ClubAccount origin = ClubAccount.builder()
                .id(2L)
                .name("Banco")
                .currentBalance(new BigDecimal("200.00"))
                .build();
        MoneyExpense expense = MoneyExpense.builder()
                .id(500L)
                .approved(Boolean.FALSE)
                .build();
        MoneyTransaction request = MoneyTransaction.builder()
                .type(MoneyTransactionType.EXPENSE)
                .amount(new BigDecimal("30.00"))
                .accountFromId(2L)
                .createdBy(44L)
                .relatedEntityId(500L)
                .build();

        when(clubAccountRepository.findById(2L)).thenReturn(Optional.of(origin));
        when(moneyExpenseRepository.findById(500L)).thenReturn(Optional.of(expense));
        when(moneyTransactionRepository.save(any())).thenAnswer(invocation -> {
            MoneyTransaction tx = invocation.getArgument(0);
            tx.setId(1234L);
            tx.setAccountFrom(origin);
            return tx;
        });

        service.recordTransaction(request);

        verify(moneyExpenseRepository).save(argThat(saved ->
                Boolean.TRUE.equals(saved.getApproved()) &&
                Long.valueOf(44L).equals(saved.getApprovedBy()) &&
                Long.valueOf(1234L).equals(saved.getTransactionId())));
    }

    @Test
    void recordTransaction_throwsWhenInsufficientFunds() {
        ClubAccount origin = ClubAccount.builder()
                .id(3L)
                .name("Caja")
                .currentBalance(new BigDecimal("10.00"))
                .build();

        MoneyTransaction request = MoneyTransaction.builder()
                .type(MoneyTransactionType.EXPENSE)
                .amount(new BigDecimal("20.00"))
                .accountFromId(3L)
                .createdBy(1L)
                .build();

        when(clubAccountRepository.findById(3L)).thenReturn(Optional.of(origin));

        assertThatThrownBy(() -> service.recordTransaction(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Fondos insuficientes");
    }
}
