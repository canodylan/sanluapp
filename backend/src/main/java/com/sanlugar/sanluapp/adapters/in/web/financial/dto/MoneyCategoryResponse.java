package com.sanlugar.sanluapp.adapters.in.web.financial.dto;

import com.sanlugar.sanluapp.domain.model.MoneyCategory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoneyCategoryResponse {
    private Long id;
    private String name;
    private String description;

    public static MoneyCategoryResponse from(MoneyCategory category) {
        return MoneyCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
