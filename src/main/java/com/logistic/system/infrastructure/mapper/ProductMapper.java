package com.logistic.system.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.logistic.system.application.dto.reponse.ProductResponse;
import com.logistic.system.application.dto.request.ProductRequest;
import com.logistic.system.domain.model.Product;
import com.logistic.system.infrastructure.persistence.entity.ProductEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    /**
     * Chuyển từ Entity (Database) sang Domain (Nghiệp vụ)
     * Thường dùng khi lấy dữ liệu lên để tính toán weight, price...
     */
    Product toDomain(ProductEntity entity);

    /**
     * Chuyển từ Domain sang Entity để lưu xuống Database
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductEntity toEntity(Product domain);

    Product toProductRequestDomain(ProductRequest request);

    ProductResponse toResponse(Product product);
}