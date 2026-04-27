package com.logistic.system.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.logistic.system.application.dto.response.StaffResponse;
import com.logistic.system.domain.model.Staff;
import com.logistic.system.infrastructure.persistence.entity.StaffEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StaffMapper {
    // Entity ↔ Domain

    Staff toDomain(StaffEntity entity);

    StaffEntity toEntity(Staff domain);

    // Domain ↔ DTO
    @Mapping(target = "email", source = "account.email")
    @Mapping(target = "phone", source = "account.phone")
    @Mapping(target = "status", source = "account.status")
    @Mapping(target = "warehouseId", source = "warehouse.warehouseId")
    StaffResponse toResponse(Staff domain);

    void updateEntityFromDomain(Staff domain, @MappingTarget StaffEntity entity);

}
