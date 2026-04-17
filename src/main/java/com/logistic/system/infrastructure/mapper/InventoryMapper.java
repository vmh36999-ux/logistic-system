package com.logistic.system.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.logistic.system.application.dto.response.InventoryResponse;
import com.logistic.system.domain.model.Inventory;
import com.logistic.system.infrastructure.persistence.entity.InventoryEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper {

    /**
     * Chuyển từ Entity (DB) sang Domain Model để xử lý logic nghiệp vụ
     */
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "warehouseId", source = "warehouse.warehouseId")
    Inventory toDomain(InventoryEntity entity);

    /**
     * Chuyển từ Domain Model ngược lại Entity để lưu xuống DB (sau khi trừ kho)
     */
    @Mapping(target = "product", ignore = true) // Cần repo để set object product nếu cần
    @Mapping(target = "warehouse", ignore = true) // Cần repo để set object warehouse nếu cần
    InventoryEntity toEntity(Inventory domain);

    /**
     * Chuyển từ Entity sang Response DTO để trả về cho Client
     */
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "warehouseId", source = "warehouse.warehouseId")
    @Mapping(target = "warehouseName", source = "warehouse.name")
    InventoryResponse toResponse(InventoryEntity entity);
}