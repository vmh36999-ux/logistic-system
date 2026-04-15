package com.logistic.system.domain.service;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.enums.DeliveryStatus;
import com.logistic.system.domain.enums.ShipmentStatus;

@Service
public class DeliveryDomainService {

    public ShipmentStatus calculateNextStatus(DeliveryStatus attemptStatus, int currentAttemptCount) {
        if (attemptStatus == DeliveryStatus.SUCCESS) {
            return ShipmentStatus.DELIVERED;
        }

        // Logic nghiệp vụ thuần túy nằm ở đây
        if (attemptStatus == DeliveryStatus.FAILED || attemptStatus == DeliveryStatus.RETRY) {
            if (currentAttemptCount >= 3) {
                return ShipmentStatus.RETURNED;
            }
        }

        return ShipmentStatus.PICKED_UP;
    }
}