package com.logistic.system.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.logistic.system.domain.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {
    private Long shipmentId;
    private Long orderId;
    private String trackingNumber;
    private Long currentWarehouseId;
    private String receiverName;
    private String receiverPhone;
    private Long receiverProvinceId;
    private String deliveryAddress;
    private BigDecimal totalWeight;
    private BigDecimal shippingFee;
    private ShipmentStatus shipmentStatus;
    private LocalDate expectedDeliveryDate;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
}
