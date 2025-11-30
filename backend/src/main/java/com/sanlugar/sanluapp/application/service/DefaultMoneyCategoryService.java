package com.sanlugar.sanluapp.application.service;

import java.util.List;
import java.util.Optional;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sanlugar.sanluapp.domain.model.MoneyCategory;
import com.sanlugar.sanluapp.domain.port.MoneyCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultMoneyCategoryService implements MoneyCategoryService {

    private final MoneyCategoryRepository moneyCategoryRepository;
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");

    @Override
    public MoneyCategory create(MoneyCategory category) {
        validate(category);
        moneyCategoryRepository.findByNameIgnoreCase(category.getName())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
                });
        category.setId(null);
        category.setColor(normalizeColor(category.getColor()));
        return moneyCategoryRepository.save(category);
    }

    @Override
    public MoneyCategory update(Long id, MoneyCategory category) {
        validate(category);
        MoneyCategory existing = moneyCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        if (!existing.getName().equalsIgnoreCase(category.getName())) {
            moneyCategoryRepository.findByNameIgnoreCase(category.getName())
                    .ifPresent(other -> {
                        throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
                    });
        }

        existing.setName(category.getName());
        existing.setColor(normalizeColor(category.getColor()));
        return moneyCategoryRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoneyCategory> findAll() {
        return moneyCategoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MoneyCategory> findById(Long id) {
        return moneyCategoryRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        moneyCategoryRepository.deleteById(id);
    }

    private void validate(MoneyCategory category) {
        if (category == null || !StringUtils.hasText(category.getName())) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        if (!StringUtils.hasText(category.getColor()) || !HEX_COLOR_PATTERN.matcher(category.getColor()).matches()) {
            throw new IllegalArgumentException("El color debe tener el formato HEX (por ejemplo #FF0000)");
        }
    }

    private String normalizeColor(String color) {
        return color == null ? null : color.toUpperCase();
    }
}
