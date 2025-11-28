package com.sanlugar.sanluapp.domain.model;

import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialUserSummary {
    private Long id;
    private String username;
    private String nickname;
    private String firstName;
    private String lastName;

    public String getDisplayName() {
        if (nickname != null && !nickname.isBlank()) {
            return nickname;
        }
        String fullName = Stream.of(firstName, lastName)
                .filter(part -> part != null && !part.isBlank())
                .reduce((left, right) -> left + " " + right)
                .orElse("");
        if (!fullName.isBlank()) {
            return fullName;
        }
        return username != null ? username : "Miembro";
    }
}
