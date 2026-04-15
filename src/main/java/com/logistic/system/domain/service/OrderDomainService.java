package com.logistic.system.domain.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.model.Order;
import com.logistic.system.domain.model.OrderItem;

@Service("orderDomainService")
public class OrderDomainService {

    public OrderDomainService() {
    }

    /**
     * Tính tổng tiền của đơn hàng dựa trên danh sách sản phẩm.
     * Công thức: sum(quantity * price_at_purchase - discount)
     */
    public void calculateTotal(Order order) {
        // ... check null items ...
        BigDecimal total = order.getItems().stream()
                .map(item -> {
                    BigDecimal itemTotal = item.getPriceAtPurchase()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    if (item.getDiscount() != null) {
                        itemTotal = itemTotal.subtract(item.getDiscount());
                    }

                    // --- DÒNG QUAN TRỌNG NHẤT: Gán giá trị vào Item ---
                    item.setSubTotal(itemTotal);

                    return itemTotal;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
    }

    /**
     * Kiểm tra tính hợp lệ của đơn hàng trước khi tạo hoặc cập nhật.
     */
    public void validateOrder(Order order) {
        if (order.getOrderCode() == null || order.getOrderCode().isBlank()) {
            order.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        // 1. Kiểm tra thông tin người nhận
        if (order.getReceiverName() == null || order.getReceiverName().isBlank()) {
            throw new RuntimeException("Tên người nhận không được để trống");
        }

        if (order.getReceiverPhone() == null || !order.getReceiverPhone().matches("^\\d{10,11}$")) {
            throw new RuntimeException("Số điện thoại người nhận không hợp lệ");
        }

        // 2. Kiểm tra địa chỉ
        if (order.getReceiverAddress() == null || order.getReceiverAddress().isBlank()) {
            throw new RuntimeException("Địa chỉ giao hàng không đầy đủ");
        }

        // 3. Kiểm tra danh sách sản phẩm
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("Đơn hàng phải có ít nhất một sản phẩm");
        }

        for (OrderItem item : order.getItems()) {
            if (item.getQuantity() <= 0) {
                throw new RuntimeException("Số lượng sản phẩm " + item.getProductId() + " phải lớn hơn 0");
            }
        }
    }
}
