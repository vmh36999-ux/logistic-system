package com.logistic.system.interfaces.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.request.TrackingLogRequest;
import com.logistic.system.application.dto.response.TrackingLogResponse;
import com.logistic.system.application.service.ShipmentTrackingApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shipments-tracking")
@RequiredArgsConstructor
@Tag(name = "Shipment Tracking", description = "Các API truy vấn hành trình và cập nhật lịch sử vận chuyển")
public class ShipmentTrackingController {

    private final ShipmentTrackingApplicationService trackingService;

    @Operation(summary = "Cập nhật hành trình", description = "Ghi nhận một cột mốc mới trong quá trình vận chuyển (ví dụ: Đã đến kho trung chuyển, Đang giao hàng...)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thành công"),
            @ApiResponse(responseCode = "400", description = "Sai quy trình chuyển trạng thái hoặc dữ liệu không hợp lệ")
    })
    @PostMapping("/update")
    public ResponseEntity<String> updateStatus(@Valid @RequestBody TrackingLogRequest request) {
        trackingService.updateStatus(
                request.getShipmentId(),
                request.getStatus(),
                request.getDescription(),
                request.getUpdatedBy());
        return ResponseEntity.ok("Cập nhật trạng thái thành công!");
    }

    @Operation(summary = "Xem lịch sử hành trình", description = "Lấy danh sách toàn bộ các mốc thời gian và trạng thái của một vận đơn")
    @GetMapping("/{id}/timeline")
    public ResponseEntity<List<TrackingLogResponse>> getTimeline(@PathVariable Long id) {
        return ResponseEntity.ok(trackingService.getShipmentTimeline(id));
    }
}