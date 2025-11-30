package com.sanlugar.sanluapp.adapters.in.web.financial;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanlugar.sanluapp.adapters.in.web.financial.dto.ClubAccountRequest;
import com.sanlugar.sanluapp.adapters.in.web.financial.dto.ClubAccountResponse;
import com.sanlugar.sanluapp.application.service.ClubAccountService;
import com.sanlugar.sanluapp.domain.model.ClubAccount;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/financial/accounts")
@RequiredArgsConstructor
@Validated
public class ClubAccountController {

    private final ClubAccountService clubAccountService;

    @PostMapping
    public ResponseEntity<ClubAccountResponse> create(@Valid @RequestBody ClubAccountRequest request) {
        ClubAccount created = clubAccountService.create(toDomain(request));
        return ResponseEntity.created(URI.create("/api/financial/accounts/" + created.getId()))
                .body(ClubAccountResponse.from(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClubAccountResponse> update(@PathVariable Long id,
            @Valid @RequestBody ClubAccountRequest request) {
        ClubAccount updated = clubAccountService.update(id, toDomain(request));
        return ResponseEntity.ok(ClubAccountResponse.from(updated));
    }

    @PatchMapping("/{id}/primary")
    public ResponseEntity<ClubAccountResponse> markPrimary(@PathVariable Long id) {
        ClubAccount updated = clubAccountService.setPrimary(id);
        return ResponseEntity.ok(ClubAccountResponse.from(updated));
    }

    @GetMapping
    public ResponseEntity<List<ClubAccountResponse>> list() {
        List<ClubAccountResponse> response = clubAccountService.findAll().stream()
                .map(ClubAccountResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubAccountResponse> get(@PathVariable Long id) {
        return clubAccountService.findById(id)
                .map(ClubAccountResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clubAccountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ClubAccount toDomain(ClubAccountRequest request) {
        return ClubAccount.builder()
                .name(request.getName())
                .description(request.getDescription())
                .currentBalance(request.getCurrentBalance())
            .primary(request.getPrimary())
                .build();
    }
}
