package com.logistic.system.domain.model;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    private Long staffId;
    private Long accountId;
    private String code;
    private String firstName;
    private String lastName;
    private Long warehouseId;
    private String address;
    private LocalDate birthDate;
    private LocalDate startDate;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
