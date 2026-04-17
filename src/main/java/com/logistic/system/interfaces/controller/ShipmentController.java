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

import com.logistic.system.application.dto.request.ShipmentRequest;
import com.logistic.system.application.dto.response.ShipmentResponse;
import com.logistic.system.application.service.ShipmentApplicationService;
import com.logistic.system.domain.enums.ShipmentStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipment Management", description = "Các API quản lý vận đơn và lộ trình vận chuyển")
public class ShipmentController {

    private final ShipmentApplicationService shipmentApplicationService;

    @Operation(summary = "Tạo vận đơn mới", description = "Khởi tạo một vận đơn (Shipment) cho đơn hàng đã đặt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo vận đơn thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc đơn hàng không tồn tại")
    })
    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(@Valid @RequestBody ShipmentRequest request) {
        ShipmentResponse response = shipmentApplicationService.createShipment(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cập nhật trạng thái vận đơn", description = "Thay đổi trạng thái của vận đơn (ví dụ: PICKED_UP, DELIVERING, ...)")
    @PutMapping("/{shipmentId}/status")
    public ResponseEntity<ShipmentResponse> updateStatus(
            @PathVariable Long shipmentId,
            @RequestParam ShipmentStatus status) {
        ShipmentResponse response = shipmentApplicationService.updateShipmentStatus(shipmentId, status);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lấy chi tiết vận đơn", description = "Truy vấn thông tin chi tiết của một vận đơn dựa trên ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy vận đơn"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy vận đơn")
    })
    @GetMapping("/{shipmentId}")
    public ResponseEntity<ShipmentResponse> getShipment(@PathVariable Long shipmentId) {
        ShipmentResponse response = shipmentApplicationService.getShipment(shipmentId);
        return ResponseEntity.ok(response);
    }
}
