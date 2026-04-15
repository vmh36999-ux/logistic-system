package com.logistic.system.interfaces.dto.request;

public interface InventoryRequest {
    Long getProductId();

    Integer getQuantity();

    Long getWarehouseId();
}