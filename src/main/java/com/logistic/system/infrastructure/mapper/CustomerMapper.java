package com.logistic.system.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.logistic.system.application.dto.reponse.CustomerResponse;
import com.logistic.system.domain.model.Customer;
import com.logistic.system.infrastructure.persistence.entity.CustomerEntity;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "accountId", source = "account.accountId")
    @Mapping(target = "email", source = "account.email")
    @Mapping(target = "phone", source = "account.phone")
    @Mapping(target = "status", source = "account.status")
    @Mapping(target = "provinceId", source = "province.provinceId")
    @Mapping(target = "districtId", source = "district.districtId")
    @Mapping(target = "wardId", source = "ward.wardId")
    Customer toDomain(CustomerEntity entity);

    // FIX TẠI ĐÂY: Ignore các Object thực thể để tránh lỗi Unmapped
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "province", ignore = true)
    @Mapping(target = "district", ignore = true)
    @Mapping(target = "ward", ignore = true)
    CustomerEntity toEntity(Customer domain);

    CustomerResponse toResponse(Customer domain);

    // FIX TẠI ĐÂY: Cần ignore tương tự chiều toEntity
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "province", ignore = true)
    @Mapping(target = "district", ignore = true)
    @Mapping(target = "ward", ignore = true)
    void updateEntityFromDomain(Customer domain, @MappingTarget CustomerEntity entity);
}