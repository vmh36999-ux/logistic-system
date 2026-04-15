package com.logistic.system.application.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String sku;
    private Double weightGram; // Rất quan trọng cho Shipment
    private BigDecimal basePrice; // Nên dùng BigDecimal cho tiền tệ thay vì Double
    private String description;
    private Long categoryId;
}
