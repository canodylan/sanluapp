package com.sanlugar.sanluapp.adapters.in.web.financial;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanlugar.sanluapp.adapters.in.web.financial.dto.MoneyTransactionRequest;
import com.sanlugar.sanluapp.adapters.in.web.financial.dto.MoneyTransactionResponse;
import com.sanlugar.sanluapp.application.service.MoneyTransactionService;
import com.sanlugar.sanluapp.domain.model.MoneyTransaction;
import com.sanlugar.sanluapp.domain.model.MoneyTransactionFilter;
import com.sanlugar.sanluapp.domain.model.MoneyTransactionType;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/financial/transactions")
@RequiredArgsConstructor
@Validated
public class MoneyTransactionController {

    private final MoneyTransactionService moneyTransactionService;

    @PostMapping
    public ResponseEntity<MoneyTransactionResponse> record(@Valid @RequestBody MoneyTransactionRequest request) {
        MoneyTransaction created = moneyTransactionService.recordTransaction(toDomain(request));
        return ResponseEntity.created(URI.create("/api/financial/transactions/" + created.getId()))
                .body(MoneyTransactionResponse.from(created));
    }

    @GetMapping
    public ResponseEntity<List<MoneyTransactionResponse>> search(
            @RequestParam(name = "type", required = false) MoneyTransactionType type,
            @RequestParam(name = "accountId", required = false) Long accountId,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "createdBy", required = false) Long createdBy,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        MoneyTransactionFilter filter = MoneyTransactionFilter.builder()
                .type(type)
                .accountId(accountId)
                .categoryId(categoryId)
                .createdBy(createdBy)
                .fromDate(fromDate)
                .toDate(toDate)
                .build();
        List<MoneyTransactionResponse> response = moneyTransactionService.findByFilter(filter).stream()
                .map(MoneyTransactionResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoneyTransactionResponse> get(@PathVariable Long id) {
        return moneyTransactionService.findById(id)
                .map(MoneyTransactionResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private MoneyTransaction toDomain(MoneyTransactionRequest request) {
        return MoneyTransaction.builder()
                .type(request.getType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .accountFromId(request.getAccountFromId())
                .accountToId(request.getAccountToId())
                .transactionDate(request.getTransactionDate())
                .createdBy(request.getCreatedBy())
                .relatedEntityId(request.getRelatedExpenseId())
                .build();
    }
}
