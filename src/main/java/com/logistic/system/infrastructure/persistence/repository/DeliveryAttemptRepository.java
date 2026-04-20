package com.logistic.system.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistic.system.infrastructure.persistence.entity.DeliveryAttemptEntity;

@Repository
public interface DeliveryAttemptRepository extends JpaRepository<DeliveryAttemptEntity, Long> {
    /**
     * Tìm tất cả các lần giao hàng của một vận đơn cụ thể.
     * Dùng để hiển thị lịch sử giao hàng.
     */
    List<DeliveryAttemptEntity> findByShipment_ShipmentIdOrderByAttemptNumberAsc(Long shipmentId);

    /**
     * Tìm các lần giao hàng thực hiện bởi một nhân viên cụ thể.
     */
    List<DeliveryAttemptEntity> findByStaff_StaffId(Long staffId);

    /**
     * Tìm lần giao hàng mới nhất của một vận đơn.
     */
    List<DeliveryAttemptEntity> findTopByShipment_ShipmentIdOrderByAttemptNumberDesc(Long shipmentId);

    /**
     * Tìm các lần giao hàng theo trạng thái (ví dụ: FAILED, DELIVERED).
     */
    List<DeliveryAttemptEntity> findByStatus(String status);

    // đếm số lần giao hàng thành công của một vận đơn
    Long countByStatusAndShipment_ShipmentId(String status, Long shipmentId);

}
