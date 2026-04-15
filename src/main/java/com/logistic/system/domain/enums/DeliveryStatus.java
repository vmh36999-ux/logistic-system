package com.logistic.system.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {
    SUCCESS("Giao thành công"),
    FAILED("Giao thất bại"),
    RETRY("Giao lại"),
    CANCELLED("Hủy giao");

    private final String label;

    @JsonValue // Khi trả về JSON, nó sẽ hiện "Giao thành công" thay vì "SUCCESS"
    public String getLabel() {
        return label;
    }
}
