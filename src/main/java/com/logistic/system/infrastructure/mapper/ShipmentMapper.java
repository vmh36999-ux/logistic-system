package com.logistic.system.infrastructure.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.logistic.system.application.dto.reponse.ShipmentItemResponse;
import com.logistic.system.application.dto.reponse.ShipmentResponse;
import com.logistic.system.application.dto.reponse.TrackingLogResponse;
import com.logistic.system.application.dto.request.ShipmentItemRequest;
import com.logistic.system.application.dto.request.ShipmentRequest;
import com.logistic.system.domain.model.DeliveryAttempt;
import com.logistic.system.domain.model.Shipment;
import com.logistic.system.domain.model.ShipmentItem;
import com.logistic.system.domain.model.ShipmentTrackingLog;
import com.logistic.system.domain.model.ShippingFee;
import com.logistic.system.infrastructure.persistence.entity.DeliveryAttemptEntity;
import com.logistic.system.infrastructure.persistence.entity.ShipmentEntity;
import com.logistic.system.infrastructure.persistence.entity.ShipmentItemEntity;
import com.logistic.system.infrastructure.persistence.entity.ShipmentTrackingLogEntity;
import com.logistic.system.infrastructure.persistence.entity.ShippingFeeEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShipmentMapper {

    // Shipment
    @Mapping(target = "orderId", source = "order.orderId")
    @Mapping(target = "currentWarehouseId", source = "currentWarehouse.warehouseId")
    @Mapping(target = "receiverProvinceId", source = "receiverProvince.provinceId")
    @Mapping(target = "receiverDistrictId", source = "receiverDistrict.districtId")
    @Mapping(target = "receiverWardId", source = "receiverWard.wardId")
    Shipment toDomain(ShipmentEntity entity);

    @Mapping(target = "order.orderId", source = "orderId")
    @Mapping(target = "currentWarehouse.warehouseId", source = "currentWarehouseId")
    @Mapping(target = "receiverProvince.provinceId", source = "receiverProvinceId")
    @Mapping(target = "receiverDistrict.districtId", source = "receiverDistrictId")
    @Mapping(target = "receiverWard.wardId", source = "receiverWardId")
    ShipmentEntity toEntity(Shipment domain);

    @Mapping(target = "shipmentId", ignore = true)
    @Mapping(target = "order.orderId", source = "orderId")
    @Mapping(target = "currentWarehouse.warehouseId", source = "currentWarehouseId")
    @Mapping(target = "receiverProvince.provinceId", source = "receiverProvinceId")
    @Mapping(target = "receiverDistrict.districtId", source = "receiverDistrictId")
    @Mapping(target = "receiverWard.wardId", source = "receiverWardId")
    void updateEntityFromDomain(Shipment domain, @org.mapstruct.MappingTarget ShipmentEntity entity);

    // DTO Mappings
    @Mapping(target = "shipmentId", ignore = true)
    @Mapping(target = "currentWarehouseId", source = "warehouseId")
    @Mapping(target = "totalWeight", ignore = true) // sẽ set ở ApplicationService
    @Mapping(target = "shippingFee", ignore = true)
    @Mapping(target = "shipmentStatus", ignore = true)
    @Mapping(target = "expectedDeliveryDate", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "trackingLogs", ignore = true)
    Shipment toDomain(ShipmentRequest request);

    ShipmentResponse toResponse(Shipment domain);

    // ShipmentItem
    @Mapping(target = "shipmentId", source = "shipment.shipmentId")
    @Mapping(target = "orderItemId", source = "orderItem.orderItemId")
    @Mapping(source = "orderItem.product.weightGram", target = "weightGram")
    ShipmentItem toDomain(ShipmentItemEntity entity);

    @Mapping(target = "shipment.shipmentId", source = "shipmentId")
    @Mapping(target = "orderItem.orderItemId", source = "orderItemId")
    @Mapping(target = "orderItem.product.weightGram", source = "weightGram")
    @Mapping(source = "productId", target = "productId")
    ShipmentItemEntity toEntity(ShipmentItem domain);

    @Mapping(target = "shipmentItemId", ignore = true)
    @Mapping(target = "shipment.shipmentId", source = "shipmentId")
    @Mapping(target = "orderItem.orderItemId", source = "orderItemId")
    @Mapping(source = "productId", target = "productId")
    void updateShipmentItemEntityFromDomain(ShipmentItem domain, @MappingTarget ShipmentItemEntity entity);

    @Mapping(target = "shipmentItemId", ignore = true)
    @Mapping(target = "pickedQuantity", ignore = true)
    @Mapping(target = "pickedAt", ignore = true)
    @Mapping(target = "packedQuantity", ignore = true)
    @Mapping(target = "packedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "weightGram", ignore = true)
    ShipmentItem toShipmentItemDomain(ShipmentItemRequest request);

    ShipmentItemResponse toShipmentItemResponse(ShipmentItem domain);

    // ShipmentTrackingLog
    @Mapping(target = "shipmentId", source = "shipment.shipmentId")
    ShipmentTrackingLog toDomain(ShipmentTrackingLogEntity entity);

    // 2. Ánh xạ từ Domain sang Entity
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE) // Bỏ qua các cảnh báo không khớp
    @Mapping(target = "shipment", ignore = true) // Object này mình set thủ công ở Service
    ShipmentTrackingLogEntity toEntity(ShipmentTrackingLog domain);

    TrackingLogResponse toTrackingLogResponse(ShipmentTrackingLog domain);

    java.util.List<TrackingLogResponse> toTrackingLogResponseList(java.util.List<ShipmentTrackingLog> logs);

    // DeliveryAttempt
    @Mapping(target = "shipmentId", source = "shipment.shipmentId")
    @Mapping(target = "shipmentItemId", source = "shipmentItem.shipmentItemId")
    @Mapping(target = "staffId", source = "staff.staffId")
    DeliveryAttempt toDomain(DeliveryAttemptEntity entity);

    @Mapping(target = "shipment.shipmentId", source = "shipmentId")
    @Mapping(target = "shipmentItem.shipmentItemId", source = "shipmentItemId")
    @Mapping(target = "staff.staffId", source = "staffId")
    DeliveryAttemptEntity toEntity(DeliveryAttempt domain);

    // ShippingFee
    @Mapping(target = "fromProvinceId", source = "fromProvince.provinceId")
    @Mapping(target = "toProvinceId", source = "toProvince.provinceId")
    ShippingFee toDomain(ShippingFeeEntity entity);

    @Mapping(target = "fromProvince.provinceId", source = "fromProvinceId")
    @Mapping(target = "toProvince.provinceId", source = "toProvinceId")
    ShippingFeeEntity toEntity(ShippingFee domain);

    //
    @AfterMapping
    default void linkItems(@MappingTarget ShipmentEntity entity) {
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> item.setShipment(entity));
        }
    }

}
