package com.logistic.system.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import com.logistic.system.domain.enums.DeliveryStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "delivery_attempts", indexes = {
        @Index(name = "idx_delivery_shipment", columnList = "shipment_id"),
        @Index(name = "idx_delivery_staff", columnList = "staff_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAttemptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private Long attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private ShipmentEntity shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_item_id")
    private ShipmentItemEntity shipmentItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private StaffEntity staff;

    @Builder.Default
    @Column(name = "attempt_number")
    private Integer attemptNumber = 1;

    @Builder.Default
    @Column(name = "delivered_quantity")
    private Integer deliveredQuantity = 0;

    @Builder.Default
    @Column(name = "failed_quantity")
    private Integer failedQuantity = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DeliveryStatus status;

    @Column(length = 255)
    private String reason;

    @Column(name = "image_proof_url", length = 500)
    private String imageProofUrl;

    @Column(name = "signature_url", length = 500)
    private String signatureUrl;

    @Column(name = "gps_latitude")
    private Double gpsLatitude;

    @Column(name = "gps_longitude")
    private Double gpsLongitude;

    @Column(name = "expected_time")
    private LocalDateTime expectedTime;

    @Column(name = "attempt_time")
    private LocalDateTime attemptTime;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
