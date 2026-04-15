package com.logistic.system.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.application.dto.request.InventoryRequest;
import com.logistic.system.application.dto.reponse.InventoryResponse;
import com.logistic.system.domain.service.InventoryDomainService;
import com.logistic.system.infrastructure.mapper.InventoryMapper;
import com.logistic.system.infrastructure.persistence.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryApplicationService {

    private final InventoryDomainService inventoryDomainService;
    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    /**
     * Lấy thông tin tồn kho của một sản phẩm
     */
    public InventoryResponse getInventoryStockByProductId(Long productId) {
        return inventoryRepository.findByProduct_ProductId(productId)
                .map(inventoryMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trong kho!"));
    }

    /**
     * Nhập thêm hàng vào kho (Update thủ công hoặc nhập kho)
     */
    @Transactional
    public void addStock(InventoryRequest request) {
        inventoryDomainService.increaseStock(request.getProductId(), request.getQuantity());
    }

    /**
     * Xuất kho thủ công
     */
    @Transactional
    public void reduceStock(InventoryRequest request) {
        inventoryDomainService.decreaseStock(request.getProductId(), request.getQuantity());
    }

    /**
     * Lấy tất cả tồn kho (nếu cần cho trang quản trị)
     */
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toResponse)
                .collect(Collectors.toList());
    }
}