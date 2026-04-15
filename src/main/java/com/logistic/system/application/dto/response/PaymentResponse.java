package com.logistic.system.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class PaymentResponse {
    private Long orderId;
    private String paymentCode;
    private BigDecimal amount;
    private String status; // PENDING, SUCCESS...
    private String statusLabel; // Chờ thanh toán, Thành công...
    private String payUrl; // Link để khách nhấn vào thanh toán
    private LocalDateTime createdAt;
}