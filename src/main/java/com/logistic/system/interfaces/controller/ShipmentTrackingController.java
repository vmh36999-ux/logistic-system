package com.logistic.system.interfaces.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.reponse.TrackingLogResponse;
import com.logistic.system.application.dto.request.TrackingLogRequest;
import com.logistic.system.application.service.ShipmentTrackingApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shipment-trackings")
@RequiredArgsConstructor
public class ShipmentTrackingController {

    private final ShipmentTrackingApplicationService trackingService;

    // API cho Shipper/Admin cập nhật trạng thái
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestBody TrackingLogRequest request) {

        trackingService.updateStatus(
                id,
                request.getStatus(),
                request.getDescription(),
                request.getUpdatedBy());
        return ResponseEntity.ok("Cập nhật trạng thái thành công!");
    }

    // API cho Khách hàng xem lịch sử hành trình
    @GetMapping("/{id}/timeline")
    public ResponseEntity<List<TrackingLogResponse>> getTimeline(@PathVariable Long id) {
        return ResponseEntity.ok(trackingService.getShipmentTimeline(id));
    }
}