package com.logistic.system.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PENDING("Chưa thanh toán"),
    SUCCESS("Đã thanh toán"),
    FAILED("Thất bại"),
    REFUNDED("Đã hoàn tiền");

    private final String label;
}
