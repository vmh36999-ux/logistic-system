package com.logistic.system.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.logistic.system.infrastructure.persistence.entity.WarehouseEntity;

@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, Long> {

    /**
     * Tìm kho theo mã code (Ví dụ: 'WH-LONGAN-01')
     */
    Optional<WarehouseEntity> findByCode(String code);

    /**
     * Tìm kho theo ID
     */
    @Override
    Optional<WarehouseEntity> findById(Long warehouseId);

    /**
     * Lấy danh sách các kho đang hoạt động (status = 'ACTIVE')
     */
    List<WarehouseEntity> findByStatus(String status);

    /**
     * Tìm các kho thuộc một tỉnh thành cụ thể (Dùng để tính phí ship hoặc chọn kho
     * gần nhất)
     */
    List<WarehouseEntity> findByProvinceProvinceId(Long provinceId);

    /**
     * Tìm kho theo loại kho (Ví dụ: 'COLD_STORAGE' - kho lạnh, 'GENERAL' - kho
     * chung)
     */
    List<WarehouseEntity> findByType(String type);

    /**
     * Truy vấn nâng cao: Tìm kho có độ ưu tiên cao nhất trong một khu vực
     * Hỗ trợ logic chọn kho tự động khi khách đặt hàng
     */
    @Query("SELECT w FROM WarehouseEntity w WHERE w.province.provinceId = :provinceId " +
            "AND w.status = 'ACTIVE' ORDER BY w.priority DESC")
    List<WarehouseEntity> findActiveWarehousesByProvinceOrderByPriority(Long provinceId);
}