package com.logistic.system.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.request.DeliveryAttemptRequest;
import com.logistic.system.application.service.DeliveryAttemptApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryAttemptApplicationService deliveryService;

    @PostMapping("/record-attempt")
    public ResponseEntity<String> recordAttempt(@RequestBody DeliveryAttemptRequest request) {
        try {
            // Gọi Service xử lý logic đếm attempt và cập nhật Shipment
            deliveryService.recordAttempt(request);

            return ResponseEntity.ok("Cập nhật trạng thái giao hàng thành công!");
        } catch (IllegalArgumentException e) {
            // Xử lý lỗi nếu không tìm thấy ID hoặc logic không hợp lệ
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Các lỗi hệ thống khác
            return ResponseEntity.internalServerError().body("Có lỗi xảy ra: " + e.getMessage());
        }
    }
}