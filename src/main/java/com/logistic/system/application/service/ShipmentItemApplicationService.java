package com.logistic.system.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.application.dto.request.ShipmentItemRequest;
import com.logistic.system.application.dto.response.ShipmentItemResponse;
import com.logistic.system.domain.enums.ShipmentItemStatus;
import com.logistic.system.domain.model.ShipmentItem;
import com.logistic.system.domain.service.ShipmentItemDomainService;
import com.logistic.system.infrastructure.mapper.ShipmentMapper;
import com.logistic.system.infrastructure.persistence.entity.OrderItemEntity;
import com.logistic.system.infrastructure.persistence.entity.ShipmentItemEntity;
import com.logistic.system.infrastructure.persistence.repository.OrderItemRepository;
import com.logistic.system.infrastructure.persistence.repository.ShipmentItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShipmentItemApplicationService {
    private final ShipmentItemRepository shipmentItemRepository;
    private final ShipmentMapper shipmentMapper;
    private final OrderItemRepository orderItemRepository;
    private final ShipmentItemDomainService shipmentItemDomainService;

    @Transactional
    public ShipmentItemResponse addItem(ShipmentItemRequest request) {
        // Tím OrderItem kế thừa dữ liệu
        OrderItemEntity orderItemEntity = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
        // Dùng mapper chuyển Request sang Domain (Lúc này các trường như weight đang bị
        // null)

        ShipmentItem domain = shipmentMapper.toShipmentItemDomain(request);
        // 3. Tự động điền dữ liệu (Auto-fill) từ Order sang ShipmentItem
        domain.setProductId(orderItemEntity.getProduct().getProductId());
        domain.setProductName(orderItemEntity.getProduct().getName());
        domain.setWeightGram(orderItemEntity.getProduct().getWeightGram());
        domain.setStatus(ShipmentItemStatus.PENDING);
        // Nếu Request không gửi quantity, lấy mặc định từ Order
        if (domain.getQuantity() == null || domain.getQuantity() == 0) {
            domain.setQuantity(orderItemEntity.getQuantity());
        }
        // chuyển sang entity
        ShipmentItemEntity entity = shipmentMapper.toShipmentItemEntity(domain);
        // Lưu vào database
        ShipmentItemEntity saveEntity = shipmentItemRepository.save(entity);
        return shipmentMapper.toShipmentItemResponse(shipmentMapper.toShipmentItemDomain(saveEntity));
    }

    // Cập nhật số lượng hoặc ghi chú
    @Transactional
    public ShipmentItem updateItem(Long itemId, Integer quantity, String note) {
        ShipmentItemEntity entity = shipmentItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong vận đơn"));
        // Kiểm tra số lượng cập nhật
        shipmentItemDomainService.validateUpdateQuantity(shipmentMapper.toShipmentItemDomain(entity), quantity);
        // Map update
        entity.setQuantity(quantity);
        entity.setNote(note);
        return shipmentMapper.toShipmentItemDomain(entity);
    }

    // Xóa sản phẩm khỏi vận đơn
    @Transactional
    public void deleteItem(Long itemId) {
        shipmentItemRepository.deleteById(itemId);
    }

    // Lấy danh sách sản phẩm trong một vận đơn cụ thể theo shipment
    @Transactional(readOnly = true)
    public List<ShipmentItem> getItemsByShipmentId(Long shipmentId) {
        List<ShipmentItemEntity> entities = shipmentItemRepository.findByShipment_ShipmentId(shipmentId);
        return entities.stream()
                .map(shipmentMapper::toShipmentItemDomain)
                .toList();
    }

    // Cập nhật trạng thái vận đơn
    @Transactional
    public ShipmentItem updatePickedStatus(Long itemId, Integer pickedQty) {
        // tải Entity từ database
        ShipmentItemEntity entity = shipmentItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong vận đơn"));
        // chuyển đổi sang Domain Model
        ShipmentItem model = shipmentMapper.toShipmentItemDomain(entity);
        // 3 ra lệnh cho DomainService thực hiện nghiệp vụ validate trạng thái picked
        shipmentItemDomainService.updatePickedStatus(model, pickedQty);
        model.setStatus(ShipmentItemStatus.PICKED);
        model.setPickedQuantity(pickedQty);
        model.setPickedAt(LocalDateTime.now());
        // 4 Update các thay đổi từ Model vào Entity
        shipmentMapper.updateShipmentItemEntityFromDomain(model, entity);

        return shipmentMapper.toShipmentItemDomain(shipmentItemRepository.save(entity));
    }
    @Transactional
    public ShipmentItem updatePackedStatus(Long itemId, Integer packedQty) {
        ShipmentItemEntity entity = shipmentItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong vận đơn"));
        // chuyển đổi sang Domain để làm việc
        ShipmentItem model = shipmentMapper.toShipmentItemDomain(entity);
        // gọi update packStatus
        shipmentItemDomainService.updatePackedStatus(model, packedQty);
        model.setStatus(ShipmentItemStatus.PACKED);
        model.setPackedQuantity(packedQty);
        model.setPackedAt(LocalDateTime.now());
        // 4 Update các thay đổi từ Model vào Entity
        shipmentMapper.updateShipmentItemEntityFromDomain(model, entity);
        return shipmentMapper.toShipmentItemDomain(shipmentItemRepository.save(entity));
    }

    // /**
    // * Thêm item mới vào shipment
    // */
    // @Transactional
    // public ShipmentItem addItem(Long shipmentId, Long orderItemId, Integer
    // quantity) {
    // // BƯỚC 1: Lấy orderItem để biết productIdId
    // var orderItems = orderItemApplicationService.getOrderItemById(orderItemId);
    // // BƯỚC 2: Lấy Product từ Product module và validate
    // var product =
    // productApplicationService.getProductForShipping(orderItems.getProduct().getProductId());

    // // BƯỚC 2: Tạo Domain Model và "Snapshot" cân nặng từ Product sang
    // var newItem = ShipmentItem.builder()
    // .shipmentId(shipmentId)
    // .orderItemId(orderItemId)
    // .productId(product.getProduct().getProductId())
    // .quantity(quantity)
    // .weightGram(product.getProduct().getWeightGram()) // Chép dữ liệu sang đây!
    // .build();
    // // BƯỚC 3: Lưu Item mới vào Database
    // var savedEntity =
    // shipmentItemRepository.save(shipmentMapper.toEntity(newItem));
    // var savedDomain = shipmentMapper.toDomain(savedEntity);
    // return savedDomain;
    // }
    // /**
    // * Cập nhật item trong shipment
    // */
    // @Transactional
    // public ShipmentItem updateItem(Long itemId, ShipmentItem item) {
    // ShipmentItemEntity entity = shipmentItemRepository.findById(itemId)
    // .orElseThrow(() -> new RuntimeException("ShipmentItem not found"));

    // // MapStruct update
    // shipmentMapper.updateShipmentItemEntityFromDomain(item, entity);

    // ShipmentItemEntity updated = shipmentItemRepository.save(entity);
    // return shipmentMapper.toDomain(updated);
    // }

    // /**
    // * Xóa item khỏi shipment
    // */
    // @Transactional
    // public void deleteItem(Long itemId) {
    // shipmentItemRepository.deleteById(itemId);
    // }

    // /**
    // * Lấy danh sách item theo shipmentId
    // */
    // public List<ShipmentItem> getItemsByShipment(Long shipmentId) {
    // List<ShipmentItemEntity> entities =
    // shipmentItemRepository.findByShipment_ShipmentId(shipmentId);
    // return entities.stream()
    // .map(shipmentMapper::toDomain)
    // .toList();
    // }
}
