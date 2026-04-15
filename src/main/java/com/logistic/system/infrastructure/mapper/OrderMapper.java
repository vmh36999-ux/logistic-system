package com.logistic.system.infrastructure.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.logistic.system.application.dto.reponse.OrderResponse;
import com.logistic.system.application.dto.request.OrderItemRequest;
import com.logistic.system.application.dto.request.OrderRequest;
import com.logistic.system.domain.model.Order;
import com.logistic.system.domain.model.OrderItem;
import com.logistic.system.infrastructure.persistence.entity.OrderEntity;
import com.logistic.system.infrastructure.persistence.entity.OrderItemEntity;

/**
 * OrderMapper: Chuyển đổi dữ liệu giữa Request, Domain và Entity.
 * Đã cấu hình thứ tự ưu tiên cho Java 21 và Lombok.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    // --- 1. Request -> Domain ---
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "accountId", ignore = true) // Sẽ được set từ SecurityContext hoặc Service
    Order toDomain(OrderRequest request);

    @Mapping(target = "orderItemId", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "priceAtPurchase", ignore = true)
    OrderItem toOrderItemDomain(OrderItemRequest itemRequest);

    // --- 2. Entity -> Domain (Fix lỗi Unknown Property accountId) ---
    // Sử dụng 'entity.' để chỉ định rõ nguồn gốc source
    @Mapping(target = "customerId", source = "entity.customer.customerId")
    @Mapping(target = "receiverProvinceId", source = "entity.receiverProvince.provinceId")

    // Đường dẫn chuẩn theo DB: Order -> Customer -> Account -> accountId
    @Mapping(target = "accountId", source = "entity.customer.account.accountId")
    Order toDomain(OrderEntity entity);

    @Mapping(target = "orderId", source = "entity.order.orderId")
    @Mapping(target = "productId", source = "entity.product.productId")
    OrderItem toDomain(OrderItemEntity entity);

    // --- 3. Domain -> Entity (Lưu xuống Database) ---
    @Mapping(target = "customer.customerId", source = "customerId")
    @Mapping(target = "receiverProvince.provinceId", source = "receiverProvinceId")
    OrderEntity toEntity(Order domain);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product.productId", source = "productId")
    @Mapping(target = "subTotal", source = "subTotal") // Chỉ định rõ để MapStruct không bỏ qua
    @Mapping(target = "discount", source = "discount") // Tiện tay map luôn kẻo null discount
    @Mapping(target = "weightGram", source = "weightGram") // Map luôn cân nặng
    OrderItemEntity toEntity(OrderItem domain);

    // --- 4. JPA Helper: Gắn quan hệ 2 chiều để tránh lỗi null FK khi save ---
    @AfterMapping
    default void linkOrderItems(@MappingTarget OrderEntity orderEntity) {
        if (orderEntity != null && orderEntity.getItems() != null) {
            orderEntity.getItems().forEach(item -> item.setOrder(orderEntity));
        }
    }

    // Chuyển đổi từ Domain sang DTO trả về cho Client
    OrderResponse toResponse(Order domain);

    // Tự động chuyển đổi List đơn hàng
    List<OrderResponse> toResponseList(List<Order> orders);

}