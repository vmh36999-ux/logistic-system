package com.logistic.system.domain.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.model.Product;

@Service
public class ProductDomainService {
    /**
     * Quy tắc 1: Kiểm tra tính hợp lệ của sản phẩm trước khi đưa vào hệ thống vận
     * hành
     */
    public void validateForShipping(Product product) {
        if (product.getWeightGram() == null || product.getWeightGram().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Sản phẩm " + product.getSku() + " chưa có trọng lượng hợp lệ!");
        }

        if (product.getBasePrice() == null || product.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Sản phẩm phải có giá cơ bản lớn hơn 0");
        }
    }
    // /**
    // * Quy tắc 2: Phân loại hàng hóa dựa trên trọng lượng (Dùng cho logic điều
    // phối xe)
    // * Ví dụ: Hàng nhẹ (< 2kg), Hàng trung bình (2-10kg), Hàng nặng (> 10kg)
    // */
    // public String classifyProductByWeight(Double weightGram) {
    // if (weightGram == null) return "UNKNOWN";

    // double weightKg = weightGram / 1000.0;
    // if (weightKg < 2.0) return "LIGHTWEIGHT";
    // if (weightKg <= 10.0) return "MEDIUM";
    // return "HEAVY_CARGO";
    // }

    // /**
    // * Quy tắc 3: Tính toán giá trị bảo hiểm hàng hóa (Insurance Value)
    // * Thường bằng một tỷ lệ phần trăm nhất định của base_price (ví dụ 1.5%)
    // */
    // public BigDecimal calculateInsuranceValue(BigDecimal basePrice) {
    // if (basePrice == null) return BigDecimal.ZERO;

    // // Phí bảo hiểm mặc định là 1.5% giá trị hàng
    // BigDecimal insuranceRate = new BigDecimal("0.015");
    // return basePrice.multiply(insuranceRate);
    // }

    // /**
    // * Quy tắc 4: Kiểm tra SKU chuẩn hóa
    // * Đảm bảo SKU tuân thủ format của kho (Ví dụ: WH-PROD-XXXX)
    // */
    // public boolean isValidSkuFormat(String sku) {
    // if (sku == null || sku.isBlank()) return false;
    // // Logic check Regex tùy theo quy định của công ty Thức
    // return sku.matches("^[A-Z0-9-]{5,20}$");
    // }
}
