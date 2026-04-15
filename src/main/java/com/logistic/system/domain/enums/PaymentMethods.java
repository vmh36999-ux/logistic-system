package com.logistic.system.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethods {
    CASH("Tiền mặt"),
    MOMO("MoMo");

    private final String label;
}
