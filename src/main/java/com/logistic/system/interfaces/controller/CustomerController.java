package com.logistic.system.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.reponse.CustomerResponse;
import com.logistic.system.application.dto.request.CustomerRequest;
import com.logistic.system.application.service.CustomerApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerApplicationService customerApplicationService;

    @GetMapping("/{accountId}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long accountId) {
        return ResponseEntity.ok(customerApplicationService.getCustomerInfo(accountId));
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long accountId,
            @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerApplicationService.updateCustomer(accountId, request));
    }
}
