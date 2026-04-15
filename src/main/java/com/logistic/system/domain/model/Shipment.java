package com.logistic.system.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.logistic.system.domain.enums.ShipmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {
    private Long shipmentId;
    private Long orderId;
    private String trackingNumber;
    private Long currentWarehouseId;
    private String receiverName;
    private String receiverPhone;
    private Long receiverProvinceId;
    private Long receiverDistrictId;
    private Long receiverWardId;
    private String deliveryAddress;
    private BigDecimal totalWeight;
    private BigDecimal shippingFee;
    private ShipmentStatus shipmentStatus;
    private LocalDate expectedDeliveryDate;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ShipmentItem> items;
    private List<ShipmentTrackingLog> trackingLogs;

    // getter and setter
    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Long getCurrentWarehouseId() {
        return currentWarehouseId;
    }

    public void setCurrentWarehouseId(Long currentWarehouseId) {
        this.currentWarehouseId = currentWarehouseId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public Long getReceiverProvinceId() {
        return receiverProvinceId;
    }

    public void setReceiverProvinceId(Long receiverProvinceId) {
        this.receiverProvinceId = receiverProvinceId;
    }

    public Long getReceiverDistrictId() {
        return receiverDistrictId;
    }

    public void setReceiverDistrictId(Long receiverDistrictId) {
        this.receiverDistrictId = receiverDistrictId;
    }

    public Long getReceiverWardId() {
        return receiverWardId;
    }

    public void setReceiverWardId(Long receiverWardId) {
        this.receiverWardId = receiverWardId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public ShipmentStatus getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(ShipmentStatus shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
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

    public List<ShipmentItem> getItems() {
        return items;
    }

    public void setItems(List<ShipmentItem> items) {
        this.items = items;
    }

    public List<ShipmentTrackingLog> getTrackingLogs() {
        return trackingLogs;
    }

    public void setTrackingLogs(List<ShipmentTrackingLog> trackingLogs) {
        this.trackingLogs = trackingLogs;
    }

}
