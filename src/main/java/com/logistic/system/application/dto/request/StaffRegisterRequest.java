package com.logistic.system.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffRegisterRequest {
    private String email;
    private String phone;
    private String password;
    private String firstName;
    private String lastName;
    private java.time.LocalDate birthDate;
    private Long warehouseId;
    private String address;
}
