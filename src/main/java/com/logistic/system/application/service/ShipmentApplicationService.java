package com.logistic.system.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.application.dto.request.ShipmentRequest;
import com.logistic.system.application.dto.response.ShipmentResponse;
import com.logistic.system.domain.enums.ShipmentStatus;
import com.logistic.system.domain.model.Shipment;
import com.logistic.system.domain.service.ShipmentDomainService;
import com.logistic.system.infrastructure.mapper.ShipmentMapper;
import com.logistic.system.infrastructure.persistence.entity.OrderEntity;
import com.logistic.system.infrastructure.persistence.entity.ShipmentEntity;
import com.logistic.system.infrastructure.persistence.entity.WarehouseEntity;
import com.logistic.system.infrastructure.persistence.repository.OrderRepository;
import com.logistic.system.infrastructure.persistence.repository.ShipmentRepository;
import com.logistic.system.infrastructure.persistence.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentApplicationService {
    private final ShipmentDomainService shipmentDomainService;
    private final ShipmentRepository shipmentRepository;
    private final WarehouseRepository warehouseRepository;
    private final OrderRepository orderRepository;
    private final ShipmentMapper shipmentMapper;

    /**
     * Tạo shipment mới và cập nhật phí vận chuyển vào đơn hàng.
     */
    @Transactional
    public ShipmentResponse createShipment(ShipmentRequest request) {
        // 1. Load data
        WarehouseEntity warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Kho không tồn tại!"));
        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại!"));

        // 2. Tính toán (Trọng lượng & Phí)
        BigDecimal totalWeightKg = calculateOrderWeight(order);

        // Region sourceReg = Region.fromString(warehouse.getProvince().getRegion());
        // Region destReg = Region.fromString(order.getReceiverProvince().getRegion());

        // BigDecimal fee = shippingFeeDomainService.calculateShippingFee(sourceReg,
        // destReg, totalWeightKg);

        // // 3. CẬP NHẬT ORDER (Phương án 2)
        // order.setShippingFee(fee); // Hết lỗi setShippingFee sau khi làm Bước 1
        // order.setTotalAmount(order.getTotalAmount().add(fee)); // Tổng = Tiền hàng +
        // Phí ship
        orderRepository.save(order);

        // 4. LƯU SHIPMENT
        Shipment shipment = new Shipment();
        shipment.setOrderId(order.getOrderId());
        shipment.setReceiverName(order.getReceiverName());
        shipment.setReceiverPhone(order.getReceiverPhone());
        shipment.setDeliveryAddress(order.getReceiverAddress());
        shipment.setReceiverProvinceId(order.getReceiverProvince().getProvinceId());
        shipment.setReceiverWardId(order.getReceiverWard().getWardId());
        shipment.setReceiverDistrictId(order.getReceiverDistrict().getDistrictId());
        shipment.setTotalWeight(totalWeightKg);
        // shipment.setShippingFee(order.getShippingFee());
        // shipment.setTotalAmount(order.getTotalAmount().add(order.getShippingFee()));
        ShipmentEntity entity = shipmentMapper.toEntity(shipment);
        entity.setOrder(order);
        entity.setCurrentWarehouse(warehouse);
        entity.setTrackingNumber("TK-" + System.currentTimeMillis()); // Đảm bảo không NULL
        entity.setShipmentStatus(ShipmentStatus.PENDING);

        var savedEntity = shipmentRepository.save(entity);
        // Sau khi shipment được lưu thì thực hiện trừ hàng tồn kho của sản phẩm trong
        // đơn hàng

        return shipmentMapper.toResponse(shipmentMapper.toDomain(savedEntity));
    }

    /**
     * Logic bổ trợ: Tính tổng trọng lượng từ danh sách sản phẩm trong đơn
     */
    private BigDecimal calculateOrderWeight(OrderEntity order) {
        return order.getItems().stream()
                .map(item -> {
                    // Chuyển weightGram sang BigDecimal trước khi nhân
                    BigDecimal itemWeight = item.getWeightGram();
                    BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                    return itemWeight.multiply(quantity); // Dùng .multiply() thay vì dấu *
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(1000.0), 2, RoundingMode.HALF_UP); // Thêm scale để tránh lỗi chia vô hạn
    }

    /**
     * Cập nhật trạng thái shipment
     */
    @Transactional
    public ShipmentResponse updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus) {
        ShipmentEntity entity = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vận đơn!"));

        Shipment shipment = shipmentMapper.toDomain(entity);

        // Gọi domain service để xử lý nghiệp vụ (kiểm tra logic chuyển trạng thái)
        shipmentDomainService.updateShipmentStatus(shipment, newStatus);

        // Cập nhật lại entity từ domain đã thay đổi
        shipmentMapper.updateEntityFromDomain(shipment, entity);
        ShipmentEntity updated = shipmentRepository.save(entity);

        return shipmentMapper.toResponse(shipmentMapper.toDomain(updated));
    }

    /**
     * Lấy chi tiết shipment
     */
    @Transactional(readOnly = true)
    public ShipmentResponse getShipment(Long shipmentId) {
        // Dùng câu lệnh JOIN FETCH để nạp Order ngay lập tức
        ShipmentEntity entity = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vận đơn!"));

        // Lúc này, entity.getOrder().getTotalAmount() đã có sẵn giá trị
        return shipmentMapper.toResponse(shipmentMapper.toDomain(entity));
    }

}