package com.logistic.system.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.request.DeliveryAttemptRequest;
import com.logistic.system.application.service.DeliveryAttemptApplicationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/delivery-attempts")
@RequiredArgsConstructor
public class DeliveryAttemptController {

    private final DeliveryAttemptApplicationService deliveryAttemptService;

    @PostMapping
    public ResponseEntity<String> recordAttempt(@Valid @RequestBody DeliveryAttemptRequest request) {
        // Gọi Application Service xử lý logic
        deliveryAttemptService.recordAttempt(request);
        
        // Trả về kết quả thành công
        return ResponseEntity.ok("Ghi nhận nỗ lực giao hàng thành công!");
    }
}