package com.logistic.system.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.logistic.system.domain.enums.AccountStatus;
import com.logistic.system.domain.enums.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private Long customerId;
    private Long accountId;

    // Thêm các trường lấy từ Account
    private String email;
    private String phone;
    private AccountStatus status;
    private String customerCode;
    private String firstName;
    private String lastName;
    private Long provinceId;
    private Long districtId;
    private Long wardId;
    private String defaultAddress;
    private Gender gender;
    private LocalDate birthDate;
    private Integer loyaltyPoints;
    private BigDecimal totalSpent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}