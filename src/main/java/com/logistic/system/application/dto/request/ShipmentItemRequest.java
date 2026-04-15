package com.logistic.system.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentItemRequest {
    private Long shipmentId;
    private Long orderItemId;
    private Long productId;
    private Integer quantity;
    private String note;
}
