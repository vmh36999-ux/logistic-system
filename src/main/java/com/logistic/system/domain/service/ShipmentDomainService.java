package com.logistic.system.domain.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.enums.ShipmentStatus;
import com.logistic.system.domain.model.Shipment;

@Service
public class ShipmentDomainService {

    // Transition Map để kiểm soát luồng trạng thái chặt chẽ
    private static final Map<ShipmentStatus, List<ShipmentStatus>> ALLOWED_TRANSITIONS;

    static {
        Map<ShipmentStatus, List<ShipmentStatus>> map = new HashMap<>();
        // PENDING có thể đi tiếp sang PICKED_UP hoặc FAILED (nếu không lấy được hàng)
        map.put(ShipmentStatus.PENDING, List.of(ShipmentStatus.PICKED_UP, ShipmentStatus.FAILED));

        // PICKED_UP có thể sang IN_TRANSIT (đi trung chuyển) hoặc ARRIVED_AT_WAREHOUSE
        // (đến trạm)
        map.put(ShipmentStatus.PICKED_UP,
                List.of(ShipmentStatus.IN_TRANSIT, ShipmentStatus.ARRIVED_AT_WAREHOUSE, ShipmentStatus.FAILED));

        // IN_TRANSIT có thể đến kho trung chuyển tiếp theo
        map.put(ShipmentStatus.IN_TRANSIT, List.of(ShipmentStatus.ARRIVED_AT_WAREHOUSE, ShipmentStatus.FAILED));

        // ARRIVED_AT_WAREHOUSE có thể trung chuyển tiếp hoặc bắt đầu giao tận nơi
        map.put(ShipmentStatus.ARRIVED_AT_WAREHOUSE,
                List.of(ShipmentStatus.IN_TRANSIT, ShipmentStatus.OUT_FOR_DELIVERY, ShipmentStatus.FAILED));

        // OUT_FOR_DELIVERY là bước cuối cùng trước khi giao thành công hoặc thất bại
        map.put(ShipmentStatus.OUT_FOR_DELIVERY, List.of(ShipmentStatus.DELIVERED, ShipmentStatus.FAILED));

        // FAILED có thể giao lại (OUT_FOR_DELIVERY) hoặc trả hàng (RETURNED)
        map.put(ShipmentStatus.FAILED, List.of(ShipmentStatus.OUT_FOR_DELIVERY, ShipmentStatus.RETURNED));

        ALLOWED_TRANSITIONS = Collections.unmodifiableMap(map);
    }

    public String generateShipmentItemId(String ordercode) {
        return "SHP-" + ordercode + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    /**
     * validateShipment(): Kiểm tra các quy tắc nghiệp vụ trước khi xuất kho
     */
    public void validateShipment(Shipment shipment) {
        // 1. Kiểm tra mã vận đơn (Tracking Number) không được trống
        if (shipment.getTrackingNumber() == null || shipment.getTrackingNumber().isBlank()) {
            throw new RuntimeException("Mã vận đơn không hợp lệ!");
        }

        // 2. Kiểm tra danh sách sản phẩm (Đảm bảo shipment có item)
        if (shipment.getItems() == null || shipment.getItems().isEmpty()) {
            throw new RuntimeException("Vận đơn không có sản phẩm nào để giao!");
        }
    }

    /**
     * updateShipmentStatus(): Quản lý vòng đời vận đơn qua Transition Map
     */
    public void updateShipmentStatus(Shipment shipment, ShipmentStatus newStatus) {
        ShipmentStatus currentStatus = shipment.getShipmentStatus();

        // Nếu trạng thái giống hệt nhau, không cần báo lỗi nhưng không xử lý tiếp
        if (currentStatus == newStatus) {
            return;
        }

        // Kiểm tra Transition Map
        List<ShipmentStatus> allowed = ALLOWED_TRANSITIONS.get(currentStatus);

        if (allowed == null || !allowed.contains(newStatus)) {
            String currentLabel = currentStatus != null ? currentStatus.getLabel() : "NULL";
            throw new RuntimeException("Không thể chuyển từ trạng thái [" + currentLabel +
                    "] sang [" + newStatus.getLabel() + "]!");
        }

        // Cập nhật trạng thái mới
        shipment.setShipmentStatus(newStatus);

        // Nếu là giao thành công, ghi nhận thời gian thực tế
        if (ShipmentStatus.DELIVERED.equals(newStatus)) {
            shipment.setDeliveredAt(LocalDateTime.now());
        }
    }
}