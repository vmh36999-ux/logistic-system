package com.logistic.system.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.logistic.system.domain.model.Staff;
import com.logistic.system.domain.model.Warehouse;
import com.logistic.system.infrastructure.persistence.entity.StaffEntity;
import com.logistic.system.infrastructure.persistence.entity.WarehouseEntity;

@Mapper(componentModel = "spring")
public interface UserWarehouseMapper {

    // Warehouse
    @Mapping(target = "provinceId", source = "province.provinceId")
    @Mapping(target = "districtId", source = "district.districtId")
    @Mapping(target = "wardId", source = "ward.wardId")
    Warehouse toDomain(WarehouseEntity entity);

    @Mapping(target = "province.provinceId", source = "provinceId")
    @Mapping(target = "district.districtId", source = "districtId")
    @Mapping(target = "ward.wardId", source = "wardId")
    WarehouseEntity toEntity(Warehouse domain);

    // Staff
    @Mapping(target = "accountId", source = "account.accountId")
    @Mapping(target = "warehouseId", source = "warehouse.warehouseId")
    Staff toDomain(StaffEntity entity);

    @Mapping(target = "account.accountId", source = "accountId")
    @Mapping(target = "warehouse.warehouseId", source = "warehouseId")
    StaffEntity toEntity(Staff domain);

}
