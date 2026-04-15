package com.logistic.system.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipping_fees", indexes = {
    @Index(name = "idx_shipping_fee_route", columnList = "from_province_id, to_province_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingFeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_province_id", nullable = false)
    private ProvinceEntity fromProvince;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_province_id", nullable = false)
    private ProvinceEntity toProvince;

    @Column(name = "weight_from", precision = 10, scale = 2)
    private BigDecimal weightFrom;

    @Column(name = "weight_to", precision = 10, scale = 2)
    private BigDecimal weightTo;

    @Column(name = "base_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseFee;

    @Column(name = "additional_fee_per_kg", precision = 10, scale = 2)
    private BigDecimal additionalFeePerKg;

    @Column(name = "estimated_days")
    private Integer estimatedDays;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
