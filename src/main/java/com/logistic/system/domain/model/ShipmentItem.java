package com.logistic.system.domain.model;

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
public class ShipmentItem {
    private Long shipmentItemId;
    private Long shipmentId;
    private Long orderItemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal weightGram;
    private Integer pickedQuantity;
    private LocalDateTime pickedAt;
    private Integer packedQuantity;
    private LocalDateTime packedAt;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ShipmentItemStatus status;

}
