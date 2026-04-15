package com.logistic.system.domain.model;

import com.logistic.system.domain.enums.WarehouseStatus;
import com.logistic.system.domain.enums.WarehouseType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    private Long warehouseId;
    private String code;
    private String name;
    private String address;
    private Long provinceId;
    private Long districtId;
    private Long wardId;
    private WarehouseType type;
    private Integer priority;
    private WarehouseStatus status;
}
