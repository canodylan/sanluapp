package com.sanlugar.sanluapp.adapters.in.web.financial.dto;

import com.sanlugar.sanluapp.domain.model.FinancialUserSummary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummaryDto {
    private Long id;
    private String username;
    private String nickname;
    private String firstName;
    private String lastName;
    private String displayName;

    public static UserSummaryDto from(FinancialUserSummary summary) {
        if (summary == null) {
            return null;
        }
        return UserSummaryDto.builder()
                .id(summary.getId())
                .username(summary.getUsername())
                .nickname(summary.getNickname())
                .firstName(summary.getFirstName())
                .lastName(summary.getLastName())
                .displayName(summary.getDisplayName())
                .build();
    }
}
