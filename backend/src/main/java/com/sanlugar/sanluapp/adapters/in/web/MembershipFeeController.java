package com.sanlugar.sanluapp.adapters.in.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanlugar.sanluapp.adapters.in.web.membership.dto.ApplyDiscountRequest;
import com.sanlugar.sanluapp.adapters.in.web.membership.dto.CreateMembershipFeeRequest;
import com.sanlugar.sanluapp.adapters.in.web.membership.dto.MembershipFeeResponse;
import com.sanlugar.sanluapp.adapters.in.web.membership.dto.PayMembershipFeeRequest;
import com.sanlugar.sanluapp.application.service.MembershipFeeService;
import com.sanlugar.sanluapp.domain.model.MembershipFee;
import com.sanlugar.sanluapp.domain.model.MembershipFeeStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/membership-fees")
@RequiredArgsConstructor
@Validated
public class MembershipFeeController {

    private final MembershipFeeService membershipFeeService;

    @PostMapping
    public ResponseEntity<MembershipFeeResponse> create(@Validated @RequestBody CreateMembershipFeeRequest request) {
        MembershipFee created = membershipFeeService.createMembershipFeeForYear(request.getUserId(), request.getYear(), request.getAttendanceDates());
        return ResponseEntity.created(URI.create("/api/membership-fees/" + created.getId()))
                .body(MembershipFeeResponse.from(created));
    }

    @PostMapping("/{id}/discounts")
    public ResponseEntity<MembershipFeeResponse> applyDiscount(@PathVariable Long id,
            @Validated @RequestBody ApplyDiscountRequest request) {
        MembershipFee updated = membershipFeeService.applyDiscount(id, request.getConcept(), request.getAmount());
        return ResponseEntity.ok(MembershipFeeResponse.from(updated));
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<MembershipFeeResponse> markAsPaid(@PathVariable Long id,
            @RequestBody(required = false) PayMembershipFeeRequest request) {
        MembershipFee fee = membershipFeeService.markFeeAsPaid(id, request == null ? null : request.getPaidAt());
        return ResponseEntity.ok(MembershipFeeResponse.from(fee));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MembershipFeeResponse>> findByUser(@PathVariable Long userId) {
        List<MembershipFeeResponse> response = membershipFeeService.getFeesForUser(userId).stream()
                .map(MembershipFeeResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MembershipFeeResponse>> findByStatus(@RequestParam(name = "status", required = false) MembershipFeeStatus status) {
        List<MembershipFee> fees = status == null
                ? membershipFeeService.getFeesByStatus(MembershipFeeStatus.PENDING)
                : membershipFeeService.getFeesByStatus(status);
        return ResponseEntity.ok(fees.stream().map(MembershipFeeResponse::from).toList());
    }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
}
