package com.logistic.system.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.application.dto.request.DeliveryAttemptRequest;
import com.logistic.system.domain.enums.DeliveryStatus;
import com.logistic.system.domain.enums.ShipmentStatus;
import com.logistic.system.domain.model.DeliveryAttempt;
import com.logistic.system.domain.service.DeliveryDomainService;
import com.logistic.system.infrastructure.mapper.ShipmentMapper;
import com.logistic.system.infrastructure.persistence.entity.DeliveryAttemptEntity;
import com.logistic.system.infrastructure.persistence.entity.ShipmentEntity;
import com.logistic.system.infrastructure.persistence.entity.ShipmentItemEntity;
import com.logistic.system.infrastructure.persistence.repository.DeliveryAttemptRepository;
import com.logistic.system.infrastructure.persistence.repository.ShipmentItemRepository;
import com.logistic.system.infrastructure.persistence.repository.ShipmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryAttemptApplicationService {

        private final ShipmentRepository shipmentRepository;
        private final ShipmentItemRepository shipmentItemRepository;
        private final ShipmentMapper shipmentMapper;
        private final DeliveryDomainService deliveryDomainService;
        private final DeliveryAttemptRepository attemptRepository;

        @Transactional
        public void recordAttempt(DeliveryAttemptRequest request) {
                // lấy thông tin shipment hiện tại
                ShipmentEntity entity = shipmentRepository.findById(request.getShipmentId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "không tìm thấy đơn hàng ID: " + request.getShipmentId()));
                ShipmentItemEntity Itementity = shipmentItemRepository.findById(request.getShipmentItemId())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong vận đơn"));
                // Xác định số lần giao tiếp theo
                Integer lastAttemptNumber = attemptRepository
                                .findTopByShipment_ShipmentIdOrderByAttemptNumberDesc(request.getShipmentId())
                                .stream().findFirst()
                                .map(DeliveryAttemptEntity::getAttemptNumber)
                                .orElse(0);
                int currentAttemptNumber = lastAttemptNumber + 1;
                // gọi domain service để lấy trạng thái
                ShipmentStatus newStatus = deliveryDomainService.calculateNextStatus(entity.getShipmentStatus(),
                                request.getStatus(), currentAttemptNumber);
                // Tính số lượng qty
                int totalDqty = entity.getItems().stream()
                                .mapToInt(ShipmentItemEntity::getPackedQuantity)
                                .sum();
                int totalFqty = entity.getItems().stream()
                                .mapToInt(ShipmentItemEntity::getPackedQuantity)
                                .sum();
                // Map từ request sang domain
                DeliveryAttempt domain = shipmentMapper.toDomain(request);
                // Map từ domain sang entity
                DeliveryAttemptEntity attemptEntity = shipmentMapper.toEntity(domain);
                attemptEntity.setShipment(entity);
                attemptEntity.setShipmentItem(Itementity);
                attemptEntity.setAttemptNumber(currentAttemptNumber);
                attemptEntity.setDeliveredQuantity(domain.getStatus() == DeliveryStatus.SUCCESS ? totalDqty : 0);
                attemptEntity.setFailedQuantity(domain.getStatus() == DeliveryStatus.FAILED ? totalFqty : 0);
                attemptRepository.save(attemptEntity);
                // 7. Cập nhật trạng thái Shipment
                entity.setShipmentStatus(newStatus);
                shipmentRepository.save(entity);

                // .failedQuantity(request.getStatus() == DeliveryStatus.FAILED ? totalFqty : 0)

                // 5. Tạo Entity Attempt
                // DeliveryAttemptEntity attempt = DeliveryAttemptEntity.builder()
                // .shipment(entity)
                // .attemptNumber(currentAttemptNumber)
                // .deliveredQuantity(request.getStatus() == DeliveryStatus.SUCCESS ? totalDqty
                // : 0)
                // .failedQuantity(request.getStatus() == DeliveryStatus.FAILED ? totalFqty : 0)
                // .status(request.getStatus())
                // .reason(request.getReason())
                // .imageProofUrl(request.getImageProofUrl())
                // // .gpsLatitude(request.getGpsLatitude())
                // // .gpsLongitude(request.getGpsLongitude())
                // .attemptTime(LocalDateTime.now())
                // .note(request.getNote())
                // // .staff(staffRepository.getReferenceById(request.getStaffId())) // Nếu Thức
                // // đã
                // // có StaffEntity
                // .build();
                // gọi mapstruct

                // attemptRepository.save(attempt);

        }
        // /**
        // * Xử lý trừ kho khi lấy hàng
        // */
        // private void processInventoryDeduction(ShipmentEntity shipment) {
        // log.info("Bắt đầu đồng bộ trừ kho cho đơn hàng: {}",
        // shipment.getShipmentId());
        // // Duyệt qua danh sách sản phẩm trong Shipment
        // shipment.getItems().forEach(item -> {
        // inventoryApplicationService.reduceStock(InventoryRequest.builder()
        // .productId(item.getProductId())
        // .quantity(item.getQuantity())
        // .build());
        // });
        // log.info("Xong đồng bộ trừ kho cho đơn hàng: {}", shipment.getShipmentId());
        // }

        // /**
        // * Xử lý cộng kho khi bị từ chối lấy hàng
        // */
        // private void processInventoryAddition(ShipmentEntity shipment) {
        // log.info("Bắt đầu đồng bộ cộng kho cho đơn hàng: {}",
        // shipment.getShipmentId());
        // // Duyệt qua danh sách sản phẩm trong Shipment
        // shipment.getItems().forEach(item -> {
        // inventoryApplicationService.addStock(InventoryRequest.builder()
        // .productId(item.getProductId())
        // .quantity(item.getQuantity())
        // .build());
        // });
        // log.info("Xong đồng bộ cộng kho cho đơn hàng: {}", shipment.getShipmentId());
        // }

}