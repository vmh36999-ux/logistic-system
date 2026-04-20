package com.logistic.system.domain.service;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.enums.DeliveryStatus;
import com.logistic.system.domain.enums.ShipmentStatus;
import com.logistic.system.infrastructure.persistence.entity.DeliveryAttemptEntity;
import com.logistic.system.infrastructure.persistence.entity.ShipmentItemEntity;

@Service
public class DeliveryDomainService {
    private static final int MAX_ATTEMPT_COUNT = 3;

    public ShipmentStatus calculateNextStatus(ShipmentStatus currentStatus, DeliveryStatus attemptStatus,
            int currentAttemptCount) {
        if (currentStatus == null) {
            throw new IllegalArgumentException("Trạng thái giao hàng không thể là null");
        }
        // kiểm tra tình trạng hơn đơn hàng đã giao chưa
        if (currentStatus == ShipmentStatus.DELIVERED || currentStatus == ShipmentStatus.RETURNED) {
            throw new IllegalStateException("Không thể tạo attempt cho shipment đã hoàn tất");
        }
        return switch (attemptStatus) {
            case SUCCESS -> ShipmentStatus.DELIVERED;
            case FAILED -> {
                if (currentAttemptCount >= MAX_ATTEMPT_COUNT) {
                    yield ShipmentStatus.RETURNED;
                }
                yield ShipmentStatus.OUT_FOR_DELIVERY;
            }
            case RETRY -> ShipmentStatus.OUT_FOR_DELIVERY;
            default -> throw new IllegalArgumentException("Trạng thái giao hàng không hợp lệ " + attemptStatus);
        };
    }

    public void calculateQty(ShipmentItemEntity item, DeliveryStatus status, DeliveryAttemptEntity attempt) {
        if (status == DeliveryStatus.SUCCESS) {
            attempt.setDeliveredQuantity(item.getPackedQuantity());
        } else if (status == DeliveryStatus.FAILED) {
            attempt.setFailedQuantity(item.getPackedQuantity());
        }
    }
}