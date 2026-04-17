package com.logistic.system.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.domain.enums.ShipmentStatus;
import com.logistic.system.infrastructure.persistence.entity.ShipmentEntity;

@Repository
public interface ShipmentRepository extends JpaRepository<ShipmentEntity, Long> {

    // Tìm shipment theo mã vận đơn (tracking number)
    Optional<ShipmentEntity> findByTrackingNumber(String trackingNumber);

    // Lấy tất cả shipment của một order
    List<ShipmentEntity> findByOrder_OrderId(Long orderId);

    // Lấy tất cả shipment theo trạng thái
    List<ShipmentEntity> findByShipmentStatus(ShipmentStatus status);

    // Cập nhật trạng thái shipment
    @Modifying
    @Transactional
    @Query("UPDATE ShipmentEntity s SET s.shipmentStatus = :status WHERE s.shipmentId = :shipmentId")
    int updateShipmentStatus(@Param("shipmentId") Long shipmentId, @Param("status") ShipmentStatus status);

    // Lấy shipment theo warehouse hiện tại
    List<ShipmentEntity> findByCurrentWarehouse_WarehouseId(Long warehouseId);

    /**
     * Tìm vận đơn kèm theo thông tin chi tiết của đơn hàng (JOIN FETCH).
     * Giải quyết vấn đề Lazy Loading khi Mapper cần truy cập s.order.totalAmount.
     */
    @Query("SELECT s FROM ShipmentEntity s JOIN FETCH s.order WHERE s.order.orderId = :id")
    Optional<ShipmentEntity> findWithOrderById(@Param("id") Long id);

}
