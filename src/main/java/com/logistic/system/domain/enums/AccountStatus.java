package com.logistic.system.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountStatus {
    ACTIVE("Đang hoạt động"),
    INACTIVE("Ngừng hoạt động"),
    LOCKED("Bị khóa");

    private final String label;
}
