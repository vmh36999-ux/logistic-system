package com.logistic.system.domain.service;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.enums.ShipmentStatus;

// PENDING("Chờ xử lý"),
// PICKED_UP("Đã lấy hàng"),
// IN_TRANSIT("Đang vận chuyển"),
// ARRIVED_AT_WAREHOUSE("Đã đến kho"),
// OUT_FOR_DELIVERY("Đang đi giao"),
// DELIVERED("Giao thành công"),
// FAILED("Giao thất bại"),
// RETURNED("Đã trả hàng");
@Service
public class ShipmentTrackingDomainService {

    public void validateStatusTransition(ShipmentStatus current, ShipmentStatus next) {
        if (current == next) {
            throw new IllegalArgumentException("Trạng thái mới không được trùng với trạng thái hiện tại.");
        }

        boolean isValid = switch (current) {
            case PENDING ->
                next == ShipmentStatus.PICKED_UP || next == ShipmentStatus.FAILED;

            case PICKED_UP ->
                next == ShipmentStatus.IN_TRANSIT || next == ShipmentStatus.ARRIVED_AT_WAREHOUSE;

            case IN_TRANSIT ->
                next == ShipmentStatus.ARRIVED_AT_WAREHOUSE;

            case ARRIVED_AT_WAREHOUSE ->
                next == ShipmentStatus.IN_TRANSIT || next == ShipmentStatus.OUT_FOR_DELIVERY;

            case OUT_FOR_DELIVERY ->
                next == ShipmentStatus.DELIVERED || next == ShipmentStatus.FAILED;

            case FAILED ->
                next == ShipmentStatus.OUT_FOR_DELIVERY || next == ShipmentStatus.RETURNED;

            case DELIVERED, RETURNED -> false; // Trạng thái cuối, không thể chuyển đi đâu nữa

            default -> false;
        };

        if (!isValid) {
            throw new IllegalArgumentException("Quy trình không hợp lệ: Không thể chuyển từ "
                    + current.getLabel() + " sang " + next.getLabel());
        }
    }
}