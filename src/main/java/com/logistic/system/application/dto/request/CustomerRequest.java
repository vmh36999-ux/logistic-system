package com.logistic.system.application.dto.request;

import lombok.Data;

@Data
public class CustomerRequest {
    private String firstName;
    private String lastName;
    private String address;
}