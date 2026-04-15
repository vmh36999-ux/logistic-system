package com.logistic.system.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;    
import java.util.Optional;
import com.logistic.system.infrastructure.persistence.entity.ShipmentItemEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItemEntity, Long> {

    // Lấy tất cả item theo shipmentId
    List<ShipmentItemEntity> findByShipment_ShipmentId(Long shipmentId);

    // Tìm item theo shipmentId và productId
    Optional<ShipmentItemEntity> findByShipment_ShipmentIdAndProductId(Long shipmentId, Long productId);

    @Modifying // Bắt buộc cho lệnh thay đổi dữ liệu (Update/Delete)
    @Transactional // Đảm bảo việc xóa được thực thi trong một giao dịch
    // Xóa tất cả item theo shipmentId (ví dụ khi hủy shipment)
    void deleteByShipment_ShipmentId(Long shipmentId);
}
