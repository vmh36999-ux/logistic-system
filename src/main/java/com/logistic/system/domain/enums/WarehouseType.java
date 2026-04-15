package com.logistic.system.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WarehouseType {
    HUB("Trung tâm trung chuyển (HUB)"),
    WAREHOUSE("Kho hàng"),
    MINI_HUB("Trạm giao nhận");

    private final String label;
}
