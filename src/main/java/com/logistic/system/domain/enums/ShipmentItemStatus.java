package com.logistic.system.domain.enums;

public enum ShipmentItemStatus {
    PENDING("Chờ xử lý"),
    PICKING("Đang lấy hàng"),
    PICKED("Đã lấy hàng"),
    PACKED("Đã đóng gói");

    private final String displayName;

    ShipmentItemStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}