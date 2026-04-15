package com.logistic.system.application.dto.request;

import com.logistic.system.domain.enums.DeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request để tạo DeliveryAttempt
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAttemptRequest {
    private Long shipmentId;
    private DeliveryStatus status; // SUCCESS / FAILED
    private String reason;
    private String imageProofUrl;
    private Double gpsLatitude;
    private Double gpsLongitude;
    private String note;
    private String staffId; // Chính là updatedBy
    // getter and setter

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getImageProofUrl() {
        return imageProofUrl;
    }

    public void setImageProofUrl(String imageProofUrl) {
        this.imageProofUrl = imageProofUrl;
    }

    public Double getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(Double gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public Double getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(Double gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

}