package com.logistic.system.application.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import com.logistic.system.application.dto.reponse.TrackingLogResponse;
import com.logistic.system.domain.enums.ShipmentStatus;
import com.logistic.system.domain.model.ShipmentTrackingLog;
import com.logistic.system.domain.service.ShipmentTrackingDomainService;
import com.logistic.system.infrastructure.mapper.ShipmentMapper;
import com.logistic.system.infrastructure.persistence.entity.OrderEntity;
import com.logistic.system.infrastructure.persistence.entity.ShipmentTrackingLogEntity;
import com.logistic.system.infrastructure.persistence.repository.ShipmentRepository;
import com.logistic.system.infrastructure.persistence.repository.ShipmentTrackingLogRepository;
    
@Slf4j
@Service
@RequiredArgsConstructor
public class ShipmentTrackingApplicationService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentTrackingLogRepository trackingLogRepository;
    private final ShipmentTrackingDomainService trackingDomainService;
    private final ShipmentMapper shipmentMapper;
    private final EmailApplicationService emailApplicationService;

    @Transactional
    public void updateStatus(Long shipmentId, ShipmentStatus nextStatus, String note, String updatedBy) {
        // 1. Tìm kiện hàng để lấy trạng thái hiện tại
        var shipmentEntity = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Shipment ID: " + shipmentId));

        // 2. Kiểm tra luật nghiệp vụ (Domain Service)
        // Nếu chuyển trạng thái sai quy trình, Exception sẽ bắn ra và Rollback toàn bộ
        trackingDomainService.validateStatusTransition(shipmentEntity.getShipmentStatus(), nextStatus);

        // 3. Tạo dữ liệu Log
        // Mình dùng Builder của Domain Model cho sạch code
        var domainLog = ShipmentTrackingLog.builder()
                .status(nextStatus)
                .description(note != null ? note : nextStatus.getLabel())
                .updatedBy(updatedBy)
                .location("Trạm trung chuyển") // Sau này Thức có thể lấy từ Request
                .build();

        // 4. Map sang Entity
        var logEntity = shipmentMapper.toEntity(domainLog);

        // 5. Liên kết Log với Shipment (Giải quyết gạch đỏ Mapper trước đó)
        logEntity.setShipment(shipmentEntity);

        // 6. Thực thi lưu trữ song song
        trackingLogRepository.save(logEntity); // Ghi lịch sử

        shipmentEntity.setShipmentStatus(nextStatus); // Cập nhật trạng thái hiện tại

        // Nếu là trạng thái đã giao hàng, ghi nhận thời gian thực tế
        if (ShipmentStatus.DELIVERED.equals(nextStatus)) {
            shipmentEntity.setDeliveredAt(LocalDateTime.now());
        }

        shipmentRepository.save(shipmentEntity);

        // 7. Gửi mail thông báo cho khách
        if (isNotifiableStatus(nextStatus)) {
            try {
                // Sử dụng Optional để tránh NullPointerException khi lấy dữ liệu sâu
                Optional.ofNullable(shipmentEntity.getOrder())
                        .map(OrderEntity::getCustomer)
                        .ifPresent(customer -> {
                            String email = customer.getAccount() != null ? customer.getAccount().getEmail() : null;
                            if (StringUtils.hasText(email)) {
                                String fullName = customer.getFirstName() + " " + customer.getLastName();

                                emailApplicationService.sendShipmentStatusEmail(
                                        email,
                                        fullName,
                                        shipmentEntity.getTrackingNumber(),
                                        nextStatus.getLabel());
                            }
                        });
            } catch (Exception e) {
                log.error("Không thể gửi email thông báo cho Shipment {}: {}", shipmentEntity.getTrackingNumber(),
                        e.getMessage());
            }
        }
            
    }
    // Hàm phụ để lọc trạng thái
    private boolean isNotifiableStatus(ShipmentStatus status) {
        return List.of(ShipmentStatus.PICKED_UP,
                ShipmentStatus.OUT_FOR_DELIVERY,
                ShipmentStatus.DELIVERED,
                ShipmentStatus.FAILED,
                ShipmentStatus.RETURNED).contains(status);
    }

    @Transactional(readOnly = true)
    public List<TrackingLogResponse> getShipmentTimeline(Long shipmentId) {
        // 1. Kiểm tra xem Shipment có tồn tại không trước khi lấy log (Optional nhưng
        // nên có)
        if (!shipmentRepository.existsById(shipmentId)) {
            throw new RuntimeException("Không tìm thấy Shipment với ID: " + shipmentId);
        }

        // 2. Lấy danh sách Log từ Repository (đã sắp xếp mới nhất lên đầu)
        List<ShipmentTrackingLogEntity> logs = trackingLogRepository
                .findByShipment_ShipmentIdOrderByCreatedAtDesc(shipmentId);

        // 3. Chuyển đổi từ Entity sang Domain rồi sang Response DTO để tuân thủ Clean
        // Architecture
        return logs.stream()
                .map(shipmentMapper::toDomain) // Entity -> Domain
                .map(shipmentMapper::toTrackingLogResponse) // Domain -> Response
                .toList();
    }
}