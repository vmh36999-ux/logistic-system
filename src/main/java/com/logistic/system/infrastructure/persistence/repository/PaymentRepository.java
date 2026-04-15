package com.logistic.system.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistic.system.infrastructure.persistence.entity.PaymentEntity;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    /**
     * Tìm theo orderId.
     * Vì OrderEntity dùng 'orderId', ta dùng dấu gạch dưới để JPA tìm đúng field.
     */
    Optional<PaymentEntity> findByOrder_OrderId(Long orderId);

    /**
     * SỬA LỖI NÀY: Trong Entity Thức đặt tên là 'requestId' (kiểu String),
     * không phải đối tượng 'request'.
     */
    Optional<PaymentEntity> findByRequestId(String requestId);

    /**
     * SỬA LỖI NÀY: Trong Entity Thức đặt tên là 'transId' (kiểu String),
     * không phải 'transaction.trans.transId'.
     */
    Optional<PaymentEntity> findByTransId(String transId);

    /**
     * Tìm giao dịch mới nhất của một đơn hàng.
     */
    Optional<PaymentEntity> findFirstByOrder_OrderIdOrderByCreatedAtDesc(Long orderId);
}
