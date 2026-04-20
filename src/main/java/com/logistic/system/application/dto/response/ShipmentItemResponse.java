package com.logistic.system.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.logistic.system.domain.enums.ShipmentItemStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentItemResponse {
    private Long shipmentItemId;
    private Long shipmentId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Integer pickedQuantity;
    private LocalDateTime pickedAt;
    private Integer packedQuantity;
    private LocalDateTime packedAt;
    private BigDecimal weightGram;
    private String note;
    private ShipmentItemStatus status;
}
