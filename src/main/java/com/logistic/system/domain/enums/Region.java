package com.logistic.system.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Phân vùng địa lý để tính phí vận chuyển.
 * Giúp chuẩn hóa dữ liệu từ DB (ví dụ: "Miền Bắc", "North", "Bắc" -> Region.NORTH).
 */
@Getter
@RequiredArgsConstructor
public enum Region {
    NORTH("Miền Bắc", new String[]{"bắc", "north"}),
    CENTRAL("Miền Trung", new String[]{"trung", "central"}),
    SOUTH("Miền Nam", new String[]{"nam", "south"}),
    UNKNOWN("Không xác định", new String[]{});

    private final String label;
    private final String[] keywords;

    /**
     * Chuyển đổi an toàn từ chuỗi (string) sang Enum Region.
     * Sử dụng chuẩn hóa (lowercase) và tìm kiếm theo keyword để tăng độ chính xác.
     */
    public static Region fromString(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }
        
        String normalized = value.toLowerCase().trim();
        
        return Arrays.stream(values())
                .filter(region -> region != UNKNOWN)
                .filter(region -> Arrays.stream(region.keywords).anyMatch(normalized::contains))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
