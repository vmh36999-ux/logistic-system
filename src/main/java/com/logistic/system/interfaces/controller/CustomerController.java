package com.logistic.system.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.request.CustomerRequest;
import com.logistic.system.application.dto.response.CustomerResponse;
import com.logistic.system.application.service.CustomerApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Các API quản lý thông tin hồ sơ khách hàng")
public class CustomerController {

    private final CustomerApplicationService customerApplicationService;

    @Operation(summary = "Lấy hồ sơ khách hàng", description = "Lấy thông tin chi tiết hồ sơ dựa trên mã tài khoản")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tìm thấy hồ sơ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy hồ sơ khách hàng")
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long accountId) {
        return ResponseEntity.ok(customerApplicationService.getCustomerInfo(accountId));
    }

    @Operation(summary = "Cập nhật hồ sơ", description = "Cập nhật thông tin cá nhân của khách hàng")
    @PutMapping("/{accountId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long accountId,
            @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerApplicationService.updateCustomer(accountId, request));
    }
}
