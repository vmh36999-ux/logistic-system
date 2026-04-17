// package com.logistic.system.interfaces.controller;

// import java.util.List;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.logistic.system.application.dto.request.ShipmentItemRequest;
// import com.logistic.system.application.dto.response.ShipmentItemResponse;
// import
// com.logistic.system.application.service.ShipmentItemApplicationService;
// import com.logistic.system.domain.model.ShipmentItem;
// import com.logistic.system.infrastructure.mapper.ShipmentMapper;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/api/shipment-items")
// @RequiredArgsConstructor
// @Tag(name = "Shipment Item Management", description = "Các API quản lý chi
// tiết sản phẩm bên trong một vận đơn")
// public class ShipmentItemController {

// private final ShipmentItemApplicationService shipmentItemApplicationService;
// private final ShipmentMapper shipmentMapper;

// @Operation(summary = "Thêm sản phẩm vào vận đơn", description = "Bổ sung một
// sản phẩm mới vào danh sách vận chuyển của vận đơn")
// @PostMapping
// public ResponseEntity<ShipmentItemResponse> addItem(@RequestBody
// ShipmentItemRequest request) {
// ShipmentItem saved =
// shipmentItemApplicationService.addItem(request.getShipmentId(),
// request.getProductId(),
// request.getQuantity());
// return ResponseEntity.ok(shipmentMapper.toShipmentItemResponse(saved));
// }

// @Operation(summary = "Cập nhật sản phẩm trong vận đơn", description = "Thay
// đổi số lượng hoặc thông tin sản phẩm đã có trong vận đơn")
// @PutMapping("/{itemId}")
// // public ResponseEntity<ShipmentItemResponse> updateItem(
// @PathVariable Long itemId,
// @RequestBody ShipmentItemRequest request) {
// ShipmentItem domain = shipmentMapper.toShipmentItemDomain(request);
// ShipmentItem updated = shipmentItemApplicationService.updateItem(itemId,
// domain);
// return ResponseEntity.ok(shipmentMapper.toShipmentItemResponse(updated));
// }

// @Operation(summary = "Xóa sản phẩm khỏi vận đơn", description = "Gỡ bỏ hoàn
// toàn một sản phẩm khỏi danh sách vận chuyển")
// @DeleteMapping("/{itemId}")
// public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
// shipmentItemApplicationService.deleteItem(itemId);
// return ResponseEntity.noContent().build();
// }

// @Operation(summary = "Lấy danh sách sản phẩm theo vận đơn", description =
// "Truy vấn toàn bộ các mặt hàng thuộc về một vận đơn cụ thể")
// @GetMapping("/shipment/{shipmentId}")
// public ResponseEntity<List<ShipmentItemResponse>>
// getItemsByShipment(@PathVariable Long shipmentId) {
// List<ShipmentItem> items =
// shipmentItemApplicationService.getItemsByShipment(shipmentId);
// List<ShipmentItemResponse> response = items.stream()
// .map(shipmentMapper::toShipmentItemResponse)
// .toList();
// return ResponseEntity.ok(response);
// }
// }
