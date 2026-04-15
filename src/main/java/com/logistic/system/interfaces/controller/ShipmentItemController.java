package com.logistic.system.interfaces.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.reponse.ShipmentItemResponse;
import com.logistic.system.application.dto.request.ShipmentItemRequest;
import com.logistic.system.application.service.ShipmentItemApplicationService;
import com.logistic.system.domain.model.ShipmentItem;
import com.logistic.system.infrastructure.mapper.ShipmentMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shipment-items")
@RequiredArgsConstructor
public class ShipmentItemController {

    private final ShipmentItemApplicationService shipmentItemApplicationService;
    private final ShipmentMapper shipmentMapper;

    /**
     * API thêm sản phẩm vào vận đơn
     */
    @PostMapping
    public ResponseEntity<ShipmentItemResponse> addItem(@RequestBody ShipmentItemRequest request) {
        ShipmentItem saved = shipmentItemApplicationService.addItem(request.getShipmentId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(shipmentMapper.toShipmentItemResponse(saved));
    }

    /**
     * API cập nhật thông tin sản phẩm trong vận đơn
     */
    @PutMapping("/{itemId}")
    public ResponseEntity<ShipmentItemResponse> updateItem(
            @PathVariable Long itemId,
            @RequestBody ShipmentItemRequest request) {
        ShipmentItem domain = shipmentMapper.toShipmentItemDomain(request);
        ShipmentItem updated = shipmentItemApplicationService.updateItem(itemId, domain);
        return ResponseEntity.ok(shipmentMapper.toShipmentItemResponse(updated));
    }

    /**
     * API xóa sản phẩm khỏi vận đơn
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        shipmentItemApplicationService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * API lấy danh sách sản phẩm của một vận đơn
     */
    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<List<ShipmentItemResponse>> getItemsByShipment(@PathVariable Long shipmentId) {
        List<ShipmentItem> items = shipmentItemApplicationService.getItemsByShipment(shipmentId);
        List<ShipmentItemResponse> response = items.stream()
                .map(shipmentMapper::toShipmentItemResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
