package com.logistic.system.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistic.system.infrastructure.persistence.entity.ShipmentItemEntity;

@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItemEntity, Long> {
    // Lấy tất cả item theo shipmentId
    List<ShipmentItemEntity> findByShipment_ShipmentId(Long shipmentId);

    // Tìm item theo shipmentId và productId
    Optional<ShipmentItemEntity> findByShipment_ShipmentIdAndProductId(Long shipmentId, Long productId);

}
