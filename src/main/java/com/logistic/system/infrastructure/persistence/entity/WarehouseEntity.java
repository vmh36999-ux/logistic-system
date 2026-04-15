package com.logistic.system.infrastructure.persistence.entity;

import com.logistic.system.domain.enums.WarehouseStatus;
import com.logistic.system.domain.enums.WarehouseType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private Long warehouseId;

    @Column(unique = true, nullable = false, length = 20)
    private String code;

    @Column(length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private ProvinceEntity province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private DistrictEntity district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private WardEntity ward;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private WarehouseType type = WarehouseType.HUB;

    @Builder.Default
    private Integer priority = 1;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private WarehouseStatus status = WarehouseStatus.ACTIVE;
}
