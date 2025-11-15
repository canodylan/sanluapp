package com.sanlugar.sanluapp.adapters.in.web.membership.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateMembershipFeeRequest {
    @NotNull
    private Long userId;

    @NotNull
    @Min(2000)
    private Integer year;

    @NotEmpty
    private List<@NotNull LocalDate> attendanceDates;
}
