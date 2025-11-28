package com.sanlugar.sanluapp.adapters.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sanlugar.sanluapp.adapters.mappers.MoneyCategoryMapper;
import com.sanlugar.sanluapp.domain.model.MoneyCategory;
import com.sanlugar.sanluapp.domain.port.MoneyCategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class MoneyCategoryRepositoryAdapter implements MoneyCategoryRepository {

    private final SpringDataMoneyCategoryRepository categoryRepository;

    @Override
    public MoneyCategory save(MoneyCategory category) {
        MoneyCategoryEntity entity = MoneyCategoryMapper.toEntity(category);
        MoneyCategoryEntity saved = categoryRepository.save(entity);
        return MoneyCategoryMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoneyCategory> findAll() {
        return categoryRepository.findAll().stream()
                .map(MoneyCategoryMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MoneyCategory> findById(Long id) {
        return categoryRepository.findById(id).map(MoneyCategoryMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MoneyCategory> findByNameIgnoreCase(String name) {
        return categoryRepository.findByNameIgnoreCase(name).map(MoneyCategoryMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
