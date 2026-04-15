package com.logistic.system.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.logistic.system.domain.enums.Gender;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponse {
    private Long customerId;
    private String customerCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String defaultAddress;
    private Gender gender;
    private LocalDate birthDate;
    private Integer loyaltyPoints;
    private BigDecimal totalSpent;
    private String status;
}
