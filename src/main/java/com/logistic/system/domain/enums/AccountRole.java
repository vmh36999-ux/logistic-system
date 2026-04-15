package com.logistic.system.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountRole {
    ADMIN("Quản trị viên"),
    STAFF("Nhân viên"),
    CUSTOMER("Khách hàng");

    private final String label;
}
