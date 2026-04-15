package com.logistic.system.domain.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.model.ShipmentItem;

@Service
public class ShipmentItemDomainService {

    /**
     * Quy tắc 1: Cập nhật số lượng đã nhặt hàng (Picking)
     * Nhân viên kho đi lấy hàng từ kệ
     */
    public void updatePickedStatus(ShipmentItem item, int pickedQty) {
        // Validation: Không được nhặt quá số lượng yêu cầu
        if (pickedQty > item.getQuantity()) {
            throw new IllegalArgumentException("Số lượng nhặt hàng (" + pickedQty +
                    ") vượt quá yêu cầu trong đơn (" + item.getQuantity() + ")");
        }

        if (pickedQty < 0) {
            throw new IllegalArgumentException("Số lượng nhặt hàng không được âm");
        }

        item.setPickedQuantity(pickedQty);
        item.setPickedAt(LocalDateTime.now());

        if (pickedQty == item.getQuantity()) {
            item.setNote("Hoàn thành nhặt hàng (Fully Picked)");
        }
    }

    /**
     * Quy tắc 2: Cập nhật số lượng đóng gói (Packing)
     * Chỉ được đóng gói những gì đã được nhặt (Picked)
     */
    public void updatePackedStatus(ShipmentItem item, int packedQty) {
        // Validation: Không được đóng gói nhiều hơn số lượng đã nhặt
        if (packedQty > item.getPickedQuantity()) {
            throw new IllegalArgumentException("Số lượng đóng gói không được vượt quá số lượng đã nhặt từ kệ!");
        }

        item.setPackedQuantity(packedQty);
        item.setPackedAt(LocalDateTime.now());
    }

    /**
     * Quy tắc 3: Tính tổng trọng lượng của danh sách Items
     * Dùng để cập nhật ngược lại cho Shipment tổng
     */
    // gọi product service để lấy trọng lượng của product
    public BigDecimal calculateTotalWeight(List<ShipmentItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .filter(item -> item.getWeightGram() != null)
                .map(item -> item.getWeightGram().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add, (a, b) -> a.add(b));
    }

    /**
     * Quy tắc 4: Kiểm tra hoàn tất đóng gói
     * Dùng để kích hoạt trạng thái "Sẵn sàng giao hàng"
     */
    public boolean isReadyForDelivery(List<ShipmentItem> items) {
        if (items == null || items.isEmpty())
            return false;

        return items.stream().allMatch(item -> item.getPackedQuantity() != null &&
                item.getPackedQuantity().equals(item.getQuantity()));
    }
    public void validateQuantity(ShipmentItem item) {
        if (item.getQuantity() == null || item.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    public BigDecimal calculateTotalWeight(ShipmentItem item) {
        if (item.getWeightGram() == null || item.getQuantity() == null)
            return BigDecimal.ZERO;
        return item.getWeightGram().multiply(BigDecimal.valueOf(item.getQuantity()));
    }
}
