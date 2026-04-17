package com.logistic.system.application.dto.response;

import java.math.BigDecimal;
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
@NoArgsConstructor // Quan trọng cho MapStruct
@AllArgsConstructor // Quan trọng cho MapStruct
public class OrderResponse {
    private Long orderId;
    private String orderCode;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private BigDecimal totalAmount;
    private PaymentMethods paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private BigDecimal shippingFee;
    private String note;
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;
    private Long customerId;
    private List<OrderItemResponse> items;

}
