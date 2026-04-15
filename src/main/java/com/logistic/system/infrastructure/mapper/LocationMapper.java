package com.logistic.system.infrastructure.mapper;

import com.logistic.system.domain.model.District;
import com.logistic.system.domain.model.Province;
import com.logistic.system.domain.model.Ward;
import com.logistic.system.infrastructure.persistence.entity.DistrictEntity;
import com.logistic.system.infrastructure.persistence.entity.ProvinceEntity;
import com.logistic.system.infrastructure.persistence.entity.WardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    // Province
    Province toDomain(ProvinceEntity entity);
    ProvinceEntity toEntity(Province domain);

    // District
    @Mapping(target = "provinceId", source = "province.provinceId")
    District toDomain(DistrictEntity entity);
    
    @Mapping(target = "province.provinceId", source = "provinceId")
    DistrictEntity toEntity(District domain);

    // Ward
    @Mapping(target = "districtId", source = "district.districtId")
    Ward toDomain(WardEntity entity);

    @Mapping(target = "district.districtId", source = "districtId")
    WardEntity toEntity(Ward domain);
}
