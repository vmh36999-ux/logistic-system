package com.logistic.system.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistic.system.infrastructure.persistence.entity.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // tìm tất cả đơn hàng theo customerId
    Optional<OrderEntity> findByCustomer_CustomerId(Long customerId);

    // tìm tất cả đơn hàng theo accountId (đi qua quan hệ Customer)
    Optional<OrderEntity> findByCustomer_Account_AccountId(Long accountId);

    // tìm tất cả đơn hàng theo orderCode
    Optional<OrderEntity> findByOrderCode(String orderCode);
}
