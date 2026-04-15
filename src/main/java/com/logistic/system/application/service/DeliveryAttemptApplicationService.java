package com.logistic.system.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.application.dto.request.DeliveryAttemptRequest;
import com.logistic.system.application.dto.request.InventoryRequest;
import com.logistic.system.domain.enums.DeliveryStatus;
import com.logistic.system.domain.enums.ShipmentStatus;
import com.logistic.system.domain.service.DeliveryDomainService;
import com.logistic.system.infrastructure.persistence.entity.DeliveryAttemptEntity;
import com.logistic.system.infrastructure.persistence.entity.ShipmentEntity;
import com.logistic.system.infrastructure.persistence.repository.DeliveryAttemptRepository;
import com.logistic.system.infrastructure.persistence.repository.ShipmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryAttemptApplicationService {
    private final DeliveryAttemptRepository attemptRepository;
    private final ShipmentRepository shipmentRepository;
    private final DeliveryDomainService deliveryDomainService;
    private final InventoryApplicationService inventoryApplicationService;

    @Transactional
    public void recordAttempt(DeliveryAttemptRequest request) {
        // 11 tìm đơn hàng
        var shipment = shipmentRepository.findById(request.getShipmentId())
                .orElseThrow(
                        () -> new IllegalArgumentException("không tìm thấy đơn hàng ID: " + request.getShipmentId()));
        // 2 Xác định số lần giao hàng tiếp theo
        // Dùng findTop >>> Desc để lấy lần giao hàng gần nhất
        Integer lastAttemptNumber = attemptRepository
                .findTopByShipment_ShipmentIdOrderByAttemptNumberDesc(shipment.getShipmentId())
                .stream().findFirst()
                .map(DeliveryAttemptEntity::getAttemptNumber)
                .orElse(0);
        ShipmentStatus oldStatus = shipment.getShipmentStatus();
        int nextAttemptNumber = lastAttemptNumber + 1;
        // 3. Tạo Entity Attempt

        DeliveryAttemptEntity attempt = DeliveryAttemptEntity.builder()
                .shipment(shipment)
                .attemptNumber(nextAttemptNumber)
                .status(request.getStatus())
                .reason(request.getReason())
                .imageProofUrl(request.getImageProofUrl())
                .gpsLatitude(request.getGpsLatitude())
                .gpsLongitude(request.getGpsLongitude())
                .attemptTime(LocalDateTime.now())
                .note(request.getNote())
                // .staff(staffRepository.getReferenceById(request.getStaffId())) // Nếu Thức đã
                // có StaffEntity
                .build();
        attemptRepository.save(attempt);
        // 4 xử lý trạng thái bên shipment
        ShipmentStatus nextStatus;
        @SuppressWarnings("unused")
        String logDescription;
        // Thay thế toàn bộ đoạn if-else từ dòng 59 đến 65 bằng:
        nextStatus = deliveryDomainService.calculateNextStatus(request.getStatus(), nextAttemptNumber);
        // Kiểm tra điều kiện trừ kho
        if (oldStatus != ShipmentStatus.PICKED_UP && nextStatus == ShipmentStatus.PICKED_UP) {
            processInventoryDeduction(shipment);
        }
        // Kiểm tra điều kiện cộng kho
        if (oldStatus != ShipmentStatus.RETURNED && nextStatus == ShipmentStatus.RETURNED) {
            processInventoryAddition(shipment);
        }
        // Cập nhật trạng thái mới cho Shipment
        shipment.setShipmentStatus(nextStatus);

        // Nếu là giao hàng thành công, ghi nhận thời gian hoàn thành
        if (ShipmentStatus.DELIVERED.equals(nextStatus)) {
            shipment.setDeliveredAt(LocalDateTime.now());
        }

        // Lưu Shipment vào Database (Fix Bug: Trước đó chỉ lưu attempt)
        shipmentRepository.save(shipment);

        if (request.getStatus() == DeliveryStatus.SUCCESS) {
            logDescription = "Giao hàng thành công (Lần " + nextAttemptNumber + ")";
        } else {
            logDescription = "Giao hàng thất bại lần " + nextAttemptNumber + ". Lý do: " + request.getReason();
        }

        log.info("Recorded delivery attempt for shipment {}: {}, next status: {}",
                shipment.getShipmentId(), request.getStatus(), nextStatus);
    }

    /**
     * Xử lý trừ kho khi lấy hàng
     */
    private void processInventoryDeduction(ShipmentEntity shipment) {
        log.info("Bắt đầu đồng bộ trừ kho cho đơn hàng: {}", shipment.getShipmentId());
        // Duyệt qua danh sách sản phẩm trong Shipment
        shipment.getItems().forEach(item -> {
            inventoryApplicationService.reduceStock(InventoryRequest.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .build());
        });
        log.info("Xong đồng bộ trừ kho cho đơn hàng: {}", shipment.getShipmentId());
    }

    /**
     * Xử lý cộng kho khi bị từ chối lấy hàng
     */
    private void processInventoryAddition(ShipmentEntity shipment) {
        log.info("Bắt đầu đồng bộ cộng kho cho đơn hàng: {}", shipment.getShipmentId());
        // Duyệt qua danh sách sản phẩm trong Shipment
        shipment.getItems().forEach(item -> {
            inventoryApplicationService.addStock(InventoryRequest.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .build());
        });
        log.info("Xong đồng bộ cộng kho cho đơn hàng: {}", shipment.getShipmentId());
    }

}