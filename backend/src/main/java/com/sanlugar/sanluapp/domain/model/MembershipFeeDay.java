package com.sanlugar.sanluapp.domain.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipFeeDay {
    private Long id;
    private LocalDate attendanceDate;
}
