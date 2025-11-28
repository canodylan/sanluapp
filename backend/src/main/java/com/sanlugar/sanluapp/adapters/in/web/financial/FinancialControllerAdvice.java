package com.sanlugar.sanluapp.adapters.in.web.financial;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {
        ClubAccountController.class,
        MoneyCategoryController.class,
        MoneyTransactionController.class,
        MoneyExpenseController.class
})
public class FinancialControllerAdvice {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
