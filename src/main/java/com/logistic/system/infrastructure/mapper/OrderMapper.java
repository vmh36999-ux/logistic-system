package com.logistic.system.infrastructure.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.logistic.system.application.dto.request.OrderItemRequest;
import com.logistic.system.application.dto.request.OrderRequest;
import com.logistic.system.application.dto.response.OrderItemResponse;
import com.logistic.system.application.dto.response.OrderResponse;
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

    // --- 1. Domain -> Response (DTO trả về cho Client) ---
    OrderResponse toResponse(Order domain);

    OrderItemResponse toOrderItemResponse(OrderItem item);

    // Tự động chuyển đổi List đơn hàng
    List<OrderResponse> toResponseList(List<Order> orders);

    // --- 2. Request -> Domain ---
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "items", source = "items")
    Order toDomainFromRequest(OrderRequest request);

    @Mapping(target = "orderItemId", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "priceAtPurchase", ignore = true)
    @Mapping(target = "weightGram", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "subTotal", ignore = true)
    OrderItem toDomainFromItemRequest(OrderItemRequest itemRequest);

    // --- 3. Entity -> Domain ---
    @Mapping(target = "customerId", source = "customer.customerId")
    @Mapping(target = "receiverProvinceId", source = "receiverProvince.provinceId")
    @Mapping(target = "accountId", source = "customer.account.accountId")
    Order toDomainFromEntity(OrderEntity entity);

    @Mapping(target = "orderId", source = "order.orderId")
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productName", expression = "java(entity.getProduct() != null ? entity.getProduct().getName() : null)")
    OrderItem toDomainFromItemEntity(OrderItemEntity entity);

    // --- 4. Domain -> Entity (Lưu xuống Database) ---
    @Mapping(target = "customer.customerId", source = "customerId")
    @Mapping(target = "receiverProvince.provinceId", source = "receiverProvinceId")
    @Mapping(target = "shippingFee", source = "shippingFee")
    OrderEntity toEntity(Order domain);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product.productId", source = "productId")
    OrderItemEntity toEntityFromItem(OrderItem domain);

    // --- 5. JPA Helper: Gắn quan hệ 2 chiều để tránh lỗi null FK khi save ---
    @AfterMapping
    default void linkOrderItems(@MappingTarget OrderEntity orderEntity) {
        if (orderEntity != null && orderEntity.getItems() != null) {
            orderEntity.getItems().forEach(item -> item.setOrder(orderEntity));
        }
    }

}