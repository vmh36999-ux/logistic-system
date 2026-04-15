package com.logistic.system.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRequest {
    @NotNull(message = "OrderId không được để trống")
    private Long orderId;

    @NotNull(message = "WarehouseId không được để trống")
    private Long warehouseId;

}
