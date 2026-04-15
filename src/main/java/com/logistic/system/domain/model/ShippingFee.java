package com.logistic.system.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFee {
    private Long id;
    private Long fromProvinceId;
    private Long toProvinceId;
    private BigDecimal weightFrom;
    private BigDecimal weightTo;
    private BigDecimal baseFee;
    private BigDecimal additionalFeePerKg;
    private Integer estimatedDays;
    private LocalDateTime createdAt;
}
