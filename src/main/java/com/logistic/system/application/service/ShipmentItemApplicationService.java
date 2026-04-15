package com.logistic.system.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.domain.model.ShipmentItem;
import com.logistic.system.infrastructure.mapper.ShipmentMapper;
import com.logistic.system.infrastructure.persistence.entity.ShipmentItemEntity;
import com.logistic.system.infrastructure.persistence.repository.ShipmentItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShipmentItemApplicationService {

    private final ShipmentItemRepository shipmentItemRepository;
    private final ShipmentMapper shipmentMapper;
    private final ProductApplicationService productApplicationService;
    private final OrderItemApplicationService orderItemApplicationService;

    /**
     * Thêm item mới vào shipment
     */
    @Transactional
    public ShipmentItem addItem(Long shipmentId, Long orderItemId, Integer quantity) {
        // BƯỚC 1: Lấy orderItem để biết productIdId
        var orderItems = orderItemApplicationService.getOrderItemById(orderItemId);
        // BƯỚC 2: Lấy Product từ Product module và validate
        var product = productApplicationService.getProductForShipping(orderItems.get(0).getProductId());

        // BƯỚC 2: Tạo Domain Model và "Snapshot" cân nặng từ Product sang
        var newItem = ShipmentItem.builder()
                .shipmentId(shipmentId)
                .orderItemId(orderItemId)
                .productId(product.getProductId())
                .quantity(quantity)
                .weightGram(product.getWeightGram()) // Chép dữ liệu sang đây!
                .build();
        // BƯỚC 3: Lưu Item mới vào Database
        var savedEntity = shipmentItemRepository.save(shipmentMapper.toEntity(newItem));
        var savedDomain = shipmentMapper.toDomain(savedEntity);
        return savedDomain;
    }
    /**
     * Cập nhật item trong shipment
     */
    @Transactional
    public ShipmentItem updateItem(Long itemId, ShipmentItem item) {
        ShipmentItemEntity entity = shipmentItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("ShipmentItem not found"));

        // MapStruct update
        shipmentMapper.updateShipmentItemEntityFromDomain(item, entity);

        ShipmentItemEntity updated = shipmentItemRepository.save(entity);
        return shipmentMapper.toDomain(updated);
    }

    /**
     * Xóa item khỏi shipment
     */
    @Transactional
    public void deleteItem(Long itemId) {
        shipmentItemRepository.deleteById(itemId);
    }

    /**
     * Lấy danh sách item theo shipmentId
     */
    public List<ShipmentItem> getItemsByShipment(Long shipmentId) {
        List<ShipmentItemEntity> entities = shipmentItemRepository.findByShipment_ShipmentId(shipmentId);
        return entities.stream()
                .map(shipmentMapper::toDomain)
                .toList();
    }
}
