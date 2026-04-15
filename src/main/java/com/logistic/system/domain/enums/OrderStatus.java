package com.logistic.system.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("Đang chờ"),
    CONFIRMED("Đã xác nhận"),
    PROCESSING("Đang xử lý"),
    SHIPPED("Đã giao hàng"),
    DELIVERED("Đã giao thành công"),
    CANCELLED("Đã hủy"),
    PAID("Đã thanh toán");

    private final String label;
}
