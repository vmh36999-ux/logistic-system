package com.logistic.system.application.dto.response;

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
public class TrackingLogResponse {
    private Long logId;
    private Long shipmentId;
    private ShipmentStatus status;
    private String statusCode;
    private String location;
    private String description;
    private String updatedBy;
    private LocalDateTime estimatedNextTime;
    private LocalDateTime createdAt;
}
