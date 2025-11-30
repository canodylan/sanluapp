package com.sanlugar.sanluapp.adapters.in.web.financial;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanlugar.sanluapp.adapters.in.web.financial.dto.ApproveMoneyExpenseRequest;
import com.sanlugar.sanluapp.adapters.in.web.financial.dto.LinkExpenseTransactionRequest;
import com.sanlugar.sanluapp.adapters.in.web.financial.dto.MoneyExpenseAssignmentRequest;
import com.sanlugar.sanluapp.adapters.in.web.financial.dto.MoneyExpenseRequest;
import com.sanlugar.sanluapp.adapters.in.web.financial.dto.MoneyExpenseResponse;
import com.sanlugar.sanluapp.adapters.in.web.financial.dto.UpdateMoneyExpenseRequest;
import com.sanlugar.sanluapp.application.service.MoneyExpenseService;
import com.sanlugar.sanluapp.domain.model.MoneyExpense;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/financial/expenses")
@RequiredArgsConstructor
@Validated
public class MoneyExpenseController {

    private final MoneyExpenseService moneyExpenseService;

    @PostMapping
    public ResponseEntity<MoneyExpenseResponse> create(@Valid @RequestBody MoneyExpenseRequest request) {
        MoneyExpense created = moneyExpenseService.create(toDomain(request));
        return ResponseEntity.created(URI.create("/api/financial/expenses/" + created.getId()))
                .body(MoneyExpenseResponse.from(created));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MoneyExpenseResponse> update(@PathVariable Long id,
            @Valid @RequestBody UpdateMoneyExpenseRequest request) {
        MoneyExpense updated = moneyExpenseService.update(id, toDomain(request));
        return ResponseEntity.ok(MoneyExpenseResponse.from(updated));
    }

    @PatchMapping("/{id}/assignment")
    public ResponseEntity<MoneyExpenseResponse> updateAssignment(@PathVariable Long id,
            @RequestBody MoneyExpenseAssignmentRequest request) {
        MoneyExpense updated = moneyExpenseService.updateAssignment(id, request.getCategoryId(), request.getAccountId());
        return ResponseEntity.ok(MoneyExpenseResponse.from(updated));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<MoneyExpenseResponse> approve(@PathVariable Long id,
            @Valid @RequestBody ApproveMoneyExpenseRequest request) {
        MoneyExpense approved = moneyExpenseService.approve(
                id,
                request.getApprovedBy(),
                request.getCategoryId(),
                request.getAccountId());
        return ResponseEntity.ok(MoneyExpenseResponse.from(approved));
    }

    @PatchMapping("/{id}/link-transaction")
    public ResponseEntity<MoneyExpenseResponse> linkTransaction(@PathVariable Long id,
            @Valid @RequestBody LinkExpenseTransactionRequest request) {
        MoneyExpense linked = moneyExpenseService.linkToTransaction(id, request.getTransactionId());
        return ResponseEntity.ok(MoneyExpenseResponse.from(linked));
    }

    @GetMapping
    public ResponseEntity<List<MoneyExpenseResponse>> list(@RequestParam(name = "approved", required = false) Boolean approved) {
        List<MoneyExpenseResponse> response = moneyExpenseService.findByApproved(approved).stream()
                .map(MoneyExpenseResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoneyExpenseResponse> get(@PathVariable Long id) {
        return moneyExpenseService.findById(id)
                .map(MoneyExpenseResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private MoneyExpense toDomain(MoneyExpenseRequest request) {
        return MoneyExpense.builder()
                .description(request.getDescription())
                .amount(request.getAmount())
                .receiptUrl(request.getReceiptUrl())
            .categoryId(request.getCategoryId())
            .accountId(request.getAccountId())
                .requestedBy(request.getRequestedBy())
                .build();
    }

    private MoneyExpense toDomain(UpdateMoneyExpenseRequest request) {
        return MoneyExpense.builder()
                .description(request.getDescription())
                .amount(request.getAmount())
                .receiptUrl(request.getReceiptUrl())
            .categoryId(request.getCategoryId())
            .accountId(request.getAccountId())
                .build();
    }
}
