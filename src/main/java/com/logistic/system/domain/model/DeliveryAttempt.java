package com.logistic.system.domain.model;

import java.time.LocalDateTime;

import com.logistic.system.domain.enums.DeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAttempt {
    private Long attemptId;
    private Long shipmentId;
    private Long shipmentItemId;
    private Long staffId;
    private Integer attemptNumber;
    private Integer deliveredQuantity;
    private Integer failedQuantity;
    private DeliveryStatus status;
    private String reason;
    private String imageProofUrl;
    private String signatureUrl;
    // private Double gpsLatitude;
    // private Double gpsLongitude;
    private LocalDateTime expectedTime;
    private LocalDateTime attemptTime;
    private String note;
    private LocalDateTime createdAt;
}
