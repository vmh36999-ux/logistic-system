package com.logistic.system.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Modifying;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.infrastructure.persistence.entity.InventoryEntity;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {

    // Tìm tồn kho theo productId
    Optional<InventoryEntity> findByProduct_ProductId(Long productId);

    // Kiểm tra tồn kho còn đủ không
    // @Query("SELECT CASE WHEN i.quantity >= :quantity THEN true ELSE false END " +
    //         "FROM InventoryEntity i WHERE i.product.productId = :productId")
    // boolean hasEnoughStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    // Giảm tồn kho (trừ số lượng khi đặt hàng)
    // @Modifying
    // @Transactional
    // @Query("UPDATE InventoryEntity i SET i.quantity = i.quantity - :quantity " +
    //         "WHERE i.product.productId = :productId AND i.quantity >= :quantity")
    // int decreaseStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    // Tăng tồn kho (ví dụ khi hủy đơn hàng)
    // @Modifying
    // @Transactional
    // @Query("UPDATE InventoryEntity i SET i.quantity = i.quantity + :quantity " +
    //         "WHERE i.product.productId = :productId")
    // int increaseStock(@Param("productId") Long productId, @Param("quantity") int quantity);
}
