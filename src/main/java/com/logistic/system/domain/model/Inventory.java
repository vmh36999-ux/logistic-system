package com.logistic.system.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    private Long inventoryId;
    private Long productId;
    private Long warehouseId;
    private Integer quantity;
    private Integer safeStock;
    private Integer maxStock;
    private Integer reorderPoint;
    private String locationRack;
    private BigDecimal costPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    //getter and setter
    public Long getInventoryId() {
        return inventoryId;
    }
    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Long getWarehouseId() {
        return warehouseId;
    }
    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public Integer getSafeStock() {
        return safeStock;
    }
    public void setSafeStock(Integer safeStock) {
        this.safeStock = safeStock;
    }
    public Integer getMaxStock() {
        return maxStock;
    }
    public void setMaxStock(Integer maxStock) {
        this.maxStock = maxStock;
    }
    public Integer getReorderPoint() {
        return reorderPoint;
    }
    public void setReorderPoint(Integer reorderPoint) {
        this.reorderPoint = reorderPoint;
    }
    public String getLocationRack() {
        return locationRack;
    }
    public void setLocationRack(String locationRack) {
        this.locationRack = locationRack;
    }
    public BigDecimal getCostPrice() {
        return costPrice;
    }
    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
