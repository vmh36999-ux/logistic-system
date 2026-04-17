package com.logistic.system.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.logistic.system.infrastructure.persistence.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    // Tìm kiếm nhanh theo SKU nếu cần
    Optional<ProductEntity> findBySku(String sku);

    Optional<ProductEntity> findByProductId(Long productId);

    /**
     * Tìm kiếm sản phẩm theo tên hoặc SKU (không phân biệt hoa thường)
     */
    @Query("SELECT p FROM ProductEntity p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<ProductEntity> searchProducts(@Param("search") String search, Pageable pageable);
}
