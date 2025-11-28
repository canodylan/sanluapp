package com.sanlugar.sanluapp.adapters.in.web.financial;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanlugar.sanluapp.adapters.in.web.financial.dto.MoneyCategoryRequest;
import com.sanlugar.sanluapp.adapters.in.web.financial.dto.MoneyCategoryResponse;
import com.sanlugar.sanluapp.application.service.MoneyCategoryService;
import com.sanlugar.sanluapp.domain.model.MoneyCategory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/financial/categories")
@RequiredArgsConstructor
@Validated
public class MoneyCategoryController {

    private final MoneyCategoryService moneyCategoryService;

    @PostMapping
    public ResponseEntity<MoneyCategoryResponse> create(@Valid @RequestBody MoneyCategoryRequest request) {
        MoneyCategory created = moneyCategoryService.create(toDomain(request));
        return ResponseEntity.created(URI.create("/api/financial/categories/" + created.getId()))
                .body(MoneyCategoryResponse.from(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MoneyCategoryResponse> update(@PathVariable Long id,
            @Valid @RequestBody MoneyCategoryRequest request) {
        MoneyCategory updated = moneyCategoryService.update(id, toDomain(request));
        return ResponseEntity.ok(MoneyCategoryResponse.from(updated));
    }

    @GetMapping
    public ResponseEntity<List<MoneyCategoryResponse>> list() {
        List<MoneyCategoryResponse> response = moneyCategoryService.findAll().stream()
                .map(MoneyCategoryResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoneyCategoryResponse> get(@PathVariable Long id) {
        return moneyCategoryService.findById(id)
                .map(MoneyCategoryResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        moneyCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private MoneyCategory toDomain(MoneyCategoryRequest request) {
        return MoneyCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
}
