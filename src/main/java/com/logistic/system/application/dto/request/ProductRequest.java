package com.logistic.system.application.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;
    @NotBlank(message = "SKU không được để trống")
    private String sku;
    @Min(value = 1, message = "Trọng lượng không thể nhỏ hơn 0")
    private Double weightGram; // Rất quan trọng cho Shipment
    @Min(value = 1)
    private BigDecimal basePrice; // Nên dùng BigDecimal cho tiền tệ thay vì Double
    private String description;
    private Long categoryId;
}
