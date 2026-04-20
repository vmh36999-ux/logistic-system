package com.logistic.system.application.dto.response;

import lombok.Data;

@Data
public class StaffResponse {
    private String code;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private java.time.LocalDate birthDate;
    private Long warehouseId;
    private String address;
    private java.time.LocalDate startDate;
    private String status;
}
