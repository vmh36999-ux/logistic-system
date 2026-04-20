package com.logistic.system.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShipmentStatus {
    PENDING("Chờ xử lý"),
    PICKED_UP("Đã lấy hàng"),
    IN_TRANSIT("Đang vận chuyển"),
    ARRIVED_AT_WAREHOUSE("Đã đến kho"),
    OUT_FOR_DELIVERY("Đang đi giao"),
    DELIVERED("Giao thành công"),
    FAILED("Giao thất bại"),
    RETURNED("Đã trả hàng");

    private final String label;
}
// SUCCESS("Giao thành công"),
// FAILED("Giao thất bại"),
// RETRY("Giao lại"),
// CANCELLED("Hủy giao");