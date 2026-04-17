package com.logistic.system.application.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String name;
    private String sku;
    private Double weightGram;
    private BigDecimal basePrice;
    // private Integer stockQuantity;
}
