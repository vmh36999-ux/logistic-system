package com.logistic.system.application.dto.response;

import java.time.LocalDateTime;

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
    private Long orderItemId;
    private Integer quantity;
    private Integer pickedQuantity;
    private LocalDateTime pickedAt;
    private Integer packedQuantity;
    private LocalDateTime packedAt;
    private String note;
}
