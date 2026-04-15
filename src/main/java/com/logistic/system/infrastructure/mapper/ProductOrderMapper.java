// package com.logistic.system.infrastructure.mapper;

// import com.logistic.system.domain.model.Inventory;
// import com.logistic.system.domain.model.Order;
// import com.logistic.system.domain.model.OrderItem;
// import com.logistic.system.domain.model.Product;
// import com.logistic.system.infrastructure.persistence.entity.InventoryEntity;
// import com.logistic.system.infrastructure.persistence.entity.OrderEntity;
// import com.logistic.system.infrastructure.persistence.entity.OrderItemEntity;
// import com.logistic.system.infrastructure.persistence.entity.ProductEntity;
// import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;

// @Mapper(componentModel = "spring")
// public interface ProductOrderMapper {

// // Product
// Product toDomain(ProductEntity entity);
// ProductEntity toEntity(Product domain);

// // Inventory
// @Mapping(target = "productId", source = "product.productId")
// @Mapping(target = "warehouseId", source = "warehouse.warehouseId")
// Inventory toDomain(InventoryEntity entity);

// @Mapping(target = "product.productId", source = "productId")
// @Mapping(target = "warehouse.warehouseId", source = "warehouseId")
// InventoryEntity toEntity(Inventory domain);

// // Order
// @Mapping(target = "customerId", source = "customer.customerId")
// @Mapping(target = "receiverProvinceId", source =
// "receiverProvince.provinceId")
// Order toDomain(OrderEntity entity);

// @Mapping(target = "customer.customerId", source = "customerId")
// @Mapping(target = "receiverProvince.provinceId", source =
// "receiverProvinceId")
// OrderEntity toEntity(Order domain);

// // OrderItem
// @Mapping(target = "orderId", source = "order.orderId")
// @Mapping(target = "productId", source = "product.productId")
// OrderItem toDomain(OrderItemEntity entity);

// @Mapping(target = "order.orderId", source = "orderId")
// @Mapping(target = "product.productId", source = "productId")
// OrderItemEntity toEntity(OrderItem domain);
// }
