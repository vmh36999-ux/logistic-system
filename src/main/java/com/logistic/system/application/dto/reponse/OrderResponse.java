package com.logistic.system.application.dto.reponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.logistic.system.domain.enums.OrderStatus;
import com.logistic.system.domain.enums.PaymentMethods;
import com.logistic.system.domain.enums.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private String orderCode;
    private Long customerId;
    private String receiverName;
    private String receiverPhone;
    private Long receiverProvinceId;
    private String receiverAddress;
    private BigDecimal totalAmount;
    private PaymentMethods paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;
}
