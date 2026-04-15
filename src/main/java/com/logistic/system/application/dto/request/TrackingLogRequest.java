package com.logistic.system.application.dto.request;

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
public class TrackingLogRequest {
    private ShipmentStatus status;
    private String location;
    private String description;
    private String updatedBy;
    private LocalDateTime estimatedNextTime;
}