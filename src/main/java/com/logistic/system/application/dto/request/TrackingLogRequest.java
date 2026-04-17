package com.logistic.system.application.dto.request;

import com.logistic.system.domain.enums.ShipmentStatus;

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
public class TrackingLogRequest {
    private Long shipmentId;
    private ShipmentStatus status;
    private String location;
    private String description;
    private String updatedBy;

}