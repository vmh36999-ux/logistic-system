package com.logistic.system.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    private Long staffId;
    private Account account;
    private String code;
    private String firstName;
    private String lastName;
    private Warehouse warehouse;
    private String address;
    private LocalDate birthDate;
    private LocalDate startDate;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
