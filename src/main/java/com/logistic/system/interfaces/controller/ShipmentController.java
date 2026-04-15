package com.logistic.system.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.reponse.ShipmentResponse;
import com.logistic.system.application.dto.request.ShipmentRequest;
import com.logistic.system.application.service.ShipmentApplicationService;
import com.logistic.system.domain.enums.ShipmentStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentApplicationService shipmentApplicationService;

    /**
     * API tạo vận đơn mới
     */
    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(@Valid @RequestBody ShipmentRequest request) {
        // Truyền nguyên object request vào, Service sẽ tự bốc tách data
        ShipmentResponse response = shipmentApplicationService.createShipment(request);

        return ResponseEntity.ok(response);
    }

    /**
     * API cập nhật trạng thái vận đơn
     */
    @PutMapping("/{shipmentId}/status")
    public ResponseEntity<ShipmentResponse> updateStatus(
            @PathVariable Long shipmentId,
            @RequestParam ShipmentStatus status) {
        ShipmentResponse response = shipmentApplicationService.updateShipmentStatus(shipmentId, status);
        return ResponseEntity.ok(response);
    }

    /**
     * API lấy thông tin chi tiết vận đơn
     */
    @GetMapping("/{shipmentId}")
    public ResponseEntity<ShipmentResponse> getShipment(@PathVariable Long shipmentId) {
        ShipmentResponse response = shipmentApplicationService.getShipment(shipmentId);
        return ResponseEntity.ok(response);
    }
}
