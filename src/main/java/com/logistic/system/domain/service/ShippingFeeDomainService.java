package com.logistic.system.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.enums.Region;

import lombok.extern.slf4j.Slf4j;

/**
 * Domain Service xử lý logic tính phí vận chuyển theo phân vùng địa lý.
 * Tuân thủ quy tắc: Nội vùng, Liên vùng gần, Liên vùng xa.
 */
@Service
@Slf4j
public class ShippingFeeDomainService {

    // --- Cấu hình hằng số phí (Có thể đưa vào DB/Config sau này) ---
    private static final BigDecimal BASE_WEIGHT_LIMIT = new BigDecimal("1.0"); // 1kg đầu tiên
    private static final BigDecimal EXTRA_WEIGHT_UNIT = new BigDecimal("0.5"); // Đơn vị tính thêm mỗi 0.5kg
    private static final BigDecimal PRICE_PER_EXTRA_UNIT = new BigDecimal("5000"); // 5.000đ mỗi 0.5kg thêm

    // Ma trận phụ phí vùng miền (Regional Surcharge)
    private static final BigDecimal FEE_INTRA_REGION = new BigDecimal("15000"); // Nội vùng
    private static final BigDecimal FEE_INTER_REGION_NEAR = new BigDecimal("30000"); // Liên vùng gần (Bắc-Trung,
                                                                                     // Trung-Nam)
    private static final BigDecimal FEE_INTER_REGION_FAR = new BigDecimal("50000"); // Liên vùng xa (Bắc-Nam)

    /**
     * Hàm chính tính toán phí vận chuyển.
     * 
     * @param sourceRegion Miền xuất phát (từ Kho)
     * @param destRegion   Miền đích (đến khách hàng)
     * @param weightKg     Tổng trọng lượng đơn hàng (kg)
     * @return BigDecimal Tổng phí vận chuyển
     */
    public BigDecimal calculateShippingFee(Region sourceRegion, Region destRegion, BigDecimal weightKg) {
        if (sourceRegion == null || destRegion == null || weightKg == null ||
                sourceRegion == Region.UNKNOWN || destRegion == Region.UNKNOWN) {
            log.warn("Thông tin tính phí không đầy đủ hoặc không hợp lệ: source={}, dest={}, weight={}",
                    sourceRegion, destRegion, weightKg);
            return BigDecimal.ZERO;
        }

        // 1. Tính phụ phí vùng miền dựa trên Ma trận
        BigDecimal regionalSurcharge = determineRegionalSurcharge(sourceRegion, destRegion);

        // 2. Tính phí theo trọng lượng vượt mức
        BigDecimal weightExtraFee = calculateWeightExtraFee(weightKg);

        // 3. Tổng phí
        BigDecimal totalFee = regionalSurcharge.add(weightExtraFee);

        log.info("Tính phí ship: [{} -> {}], Nặng: {}kg => Phí vùng: {}, Phí cân nặng: {}, Tổng: {}",
                sourceRegion.getLabel(), destRegion.getLabel(), weightKg, regionalSurcharge, weightExtraFee, totalFee);

        return totalFee;
    }

    /**
     * Xác định phụ phí vùng miền dựa trên quan hệ địa lý
     */
    private BigDecimal determineRegionalSurcharge(Region source, Region dest) {
        if (source == dest) {
            return FEE_INTRA_REGION;
        }

        // Kiểm tra Liên vùng xa (Bắc - Nam)
        boolean isNorthSouth = (source == Region.NORTH && dest == Region.SOUTH) ||
                (source == Region.SOUTH && dest == Region.NORTH);

        if (isNorthSouth) {
            return FEE_INTER_REGION_FAR;
        }

        // Các trường hợp còn lại là Liên vùng gần (Bắc-Trung hoặc Trung-Nam)
        return FEE_INTER_REGION_NEAR;
    }

    /**
     * Tính phí cộng thêm nếu vượt quá trọng lượng cơ bản (1kg)
     * Công thức: Mỗi 0.5kg tiếp theo tính thêm 5.000đ
     */
    private BigDecimal calculateWeightExtraFee(BigDecimal weightKg) {
        if (weightKg.compareTo(BASE_WEIGHT_LIMIT) <= 0) {
            return BigDecimal.ZERO;
        }

        // Số kg vượt mức
        BigDecimal extraWeight = weightKg.subtract(BASE_WEIGHT_LIMIT);

        // Tính số đơn vị 0.5kg (Làm tròn lên - ví dụ vượt 0.1kg vẫn tính là 0.5kg)
        BigDecimal units = extraWeight.divide(EXTRA_WEIGHT_UNIT, 0, RoundingMode.CEILING);

        return units.multiply(PRICE_PER_EXTRA_UNIT);
    }
}
