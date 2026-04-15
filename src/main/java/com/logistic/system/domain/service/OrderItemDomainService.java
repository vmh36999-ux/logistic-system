package com.logistic.system.domain.service;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.model.OrderItem;

@Service
public class OrderItemDomainService {

    /**
     * Validate nghiệp vụ cho OrderItem
     */
    public void validate(OrderItem orderItem) {
        if (orderItem.getProductId() == null) {
            throw new IllegalArgumentException("OrderItem must have a productId");
        }
        if (orderItem.getQuantity() == null || orderItem.getQuantity() <= 0) {
            throw new IllegalArgumentException("OrderItem quantity must be greater than 0");
        }
        if (orderItem.getPriceAtPurchase() == null || orderItem.getPriceAtPurchase().doubleValue() < 0) {
            throw new IllegalArgumentException("OrderItem price must be non-negative");
        }
    }

    /**
     * Tính tổng tiền cho OrderItem
     */
    public void calculateTotalAmount(OrderItem orderItem) {
        if (orderItem.getPriceAtPurchase() != null && orderItem.getQuantity() != null) {
            orderItem.setSubTotal(
                    orderItem.getPriceAtPurchase().multiply(
                            java.math.BigDecimal.valueOf(orderItem.getQuantity())));
        }
    }
}
