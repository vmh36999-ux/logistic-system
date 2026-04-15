package com.logistic.system.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.infrastructure.persistence.entity.InventoryEntity;
import com.logistic.system.infrastructure.persistence.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryDomainService {

    private final InventoryRepository inventoryRepository;

    /**
     * Trừ kho: Tìm theo productId.
     * Lưu ý: Nếu có nhiều kho, bạn cần logic chọn kho (ví dụ kho mặc định).
     */
    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        // Tìm bản ghi inventory cho sản phẩm này
        // Ở đây mình giả định lấy bản ghi đầu tiên tìm thấy hoặc kho mặc định
        InventoryEntity inventory = inventoryRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm ID " + productId + " không có trong kho nào!"));

        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Kho không đủ hàng! Hiện có: " + inventory.getQuantity());
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    /**
     * Cộng lại số lượng tồn kho khi hủy đơn hàng.
     */
    @Transactional
    public void increaseStock(Long productId, Integer quantity) {
        InventoryEntity inventory = inventoryRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + productId));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
    }

    /**
     * Kiểm tra đủ hàng trước khi tạo shipment (Giai đoạn cuối).
     */
    public void validateStock(Long productId, Integer quantity) {
        InventoryEntity inventory = inventoryRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + productId));

        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException(
                    "Kho không đủ hàng để tạo vận đơn cho sản phẩm: " + inventory.getProduct().getName());
        }
    }
}