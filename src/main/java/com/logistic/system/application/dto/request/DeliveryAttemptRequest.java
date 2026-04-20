package com.logistic.system.application.dto.request;

import com.logistic.system.domain.enums.DeliveryStatus;
import com.logistic.system.domain.enums.FailureReason;

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
    // private Long staffId;
    private Long shipmentId;
    private Long shipmentItemId;
    private DeliveryStatus status; // SUCCESS / FAILED
    private FailureReason reason;
    private String imageProofUrl;
    private String note;
}