package com.logistic.system.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.logistic.system.domain.enums.OrderStatus;
import com.logistic.system.domain.enums.PaymentMethods;
import com.logistic.system.domain.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long orderId;
    private String orderCode;
    private Long customerId;
    private Long accountId;
    private String receiverName;
    private String receiverPhone;
    private Long receiverProvinceId;
    private Long receiverDistrictId;
    private Long receiverWardId;
    private String receiverAddress;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;
    private PaymentMethods paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItem> items;

}
