package com.logistic.system.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistic.system.infrastructure.persistence.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    // Tìm kiếm nhanh theo SKU nếu cần
    Optional<ProductEntity> findBySku(String sku);

    Optional<ProductEntity> findByProductId(Long productId);
}
