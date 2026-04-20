package com.logistic.system.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountStatus {
    PENDING("Chờ duyệt"), // Admin chưa duyệt
    ACTIVE("Đang hoạt động"), // Admin đã duyệt, có thể login
    REJECTED("Bị từ chối"), // Admin không duyệt hồ sơ
    INACTIVE("Ngừng hoạt động"), // Tài khoản cũ, đã nghỉ việc
    LOCKED("Bị khóa"); // Vi phạm quy định, bị Admin khóa

    private final String label;
}
