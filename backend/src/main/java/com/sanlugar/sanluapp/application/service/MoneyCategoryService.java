package com.sanlugar.sanluapp.application.service;

import java.util.List;
import java.util.Optional;

import com.sanlugar.sanluapp.domain.model.MoneyCategory;

public interface MoneyCategoryService {
    MoneyCategory create(MoneyCategory category);
    MoneyCategory update(Long id, MoneyCategory category);
    List<MoneyCategory> findAll();
    Optional<MoneyCategory> findById(Long id);
    void delete(Long id);
}
