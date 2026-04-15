package com.logistic.system.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WarehouseStatus {
    ACTIVE("Đang hoạt động"),
    INACTIVE("Ngừng hoạt động"),
    MAINTENANCE("Đang bảo trì");

    private final String label;
}
