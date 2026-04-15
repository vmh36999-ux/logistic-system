package com.logistic.system.infrastructure.persistence.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistic.system.infrastructure.persistence.entity.OrderItemEntity;
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    
    // Lấy tất cả OrderItem theo OrderId
    List<OrderItemEntity> findByOrder_OrderId(Long orderId);

    // Lấy tất cả OrderItem theo ProductId
    List<OrderItemEntity> findByProduct_ProductId(Long productId);

    // Kiểm tra xem OrderItem có tồn tại theo OrderId và ProductId
    boolean existsByOrder_OrderIdAndProduct_ProductId(Long orderId, Long productId);
}
