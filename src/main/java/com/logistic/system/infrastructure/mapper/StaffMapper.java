// package com.logistic.system.infrastructure.mapper;

// import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;

// import com.logistic.system.infrastructure.persistence.entity.StaffEntity;
// import com.logistic.system.application.dto.response.StaffResponse;
// import com.logistic.system.domain.model.Staff;
// import org.mapstruct.ReportingPolicy;

// @Mapper(componentModel = "spring", unmappedTargetPolicy =
// ReportingPolicy.IGNORE)
// public interface StaffMapper {
// // Entity ↔ Domain
// @Mapping(target = "accountId", source = "account.accountId")
// @Mapping(target = "email", source = "account.email")
// @Mapping(target = "phone", source = "account.phone")
// @Mapping(target = "status", source = "account.status")
// @Mapping(target = "warehouseId", source = "warehouse.warehouseId")
// Staff toDomain(StaffEntity entity);

// // Domain ↔ Entity
// @Mapping(target = "account.email", source = "email")
// @Mapping(target = "account.phone", source = "phone")
// @Mapping(target = "account.status", source = "status")
// @Mapping(target = "warehouse.warehouseId", source = "warehouseId")
// StaffEntity toEntity(Staff domain);

// // Domain ↔ DTO
// StaffResponse toResponse(Staff domain);

// }
