package com.logistic.system.domain.service;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.enums.ShipmentStatus;

@Service
public class ShipmentTrackingDomainService {

    public void validateStatusTransition(ShipmentStatus current, ShipmentStatus next) {
        if (current == next) {
            throw new IllegalArgumentException("Trạng thái mới không được trùng với trạng thái hiện tại.");
        }

        boolean isValid = false;
        switch (current) {
            case PENDING:
                isValid = next == ShipmentStatus.PICKED_UP || next == ShipmentStatus.FAILED;
                break;
            case PICKED_UP:
                isValid = next == ShipmentStatus.IN_TRANSIT || next == ShipmentStatus.ARRIVED_AT_WAREHOUSE;
                break;
            case IN_TRANSIT:
                isValid = next == ShipmentStatus.ARRIVED_AT_WAREHOUSE;
                break;
            case ARRIVED_AT_WAREHOUSE:
                isValid = next == ShipmentStatus.IN_TRANSIT || next == ShipmentStatus.OUT_FOR_DELIVERY;
                break;
            case OUT_FOR_DELIVERY:
                isValid = next == ShipmentStatus.DELIVERED || next == ShipmentStatus.FAILED;
                break;
            case FAILED:
                isValid = next == ShipmentStatus.OUT_FOR_DELIVERY || next == ShipmentStatus.RETURNED;
                break;
            case DELIVERED:
            case RETURNED:
                isValid = false;
                break;
            default:
                isValid = false;
                break;
        }

        if (!isValid) {
            throw new IllegalArgumentException("Quy trình không hợp lệ: Không thể chuyển từ "
                    + current.getLabel() + " sang " + next.getLabel());
        }
    }
}