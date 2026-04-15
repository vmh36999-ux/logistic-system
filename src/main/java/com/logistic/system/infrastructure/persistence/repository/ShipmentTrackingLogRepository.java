package com.logistic.system.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistic.system.infrastructure.persistence.entity.ShipmentTrackingLogEntity;

@Repository
public interface ShipmentTrackingLogRepository extends JpaRepository<ShipmentTrackingLogEntity, Long> {
    // Lấy toàn bộ lịch sử của 1 shipment, sắp xếp cái mới nhất lên trên cùng
    List<ShipmentTrackingLogEntity> findByShipment_ShipmentIdOrderByCreatedAtDesc(Long shipmentId);
}
