package com.sanlugar.sanluapp.domain.port;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.MoneyCategory;

public interface MoneyCategoryRepository {
    MoneyCategory save(MoneyCategory category);
    List<MoneyCategory> findAll();
    Optional<MoneyCategory> findById(Long id);
    Optional<MoneyCategory> findByNameIgnoreCase(String name);
    void deleteById(Long id);
}
