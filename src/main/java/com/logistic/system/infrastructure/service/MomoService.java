package com.logistic.system.infrastructure.service;

import java.math.BigDecimal;
import java.util.Map;

import com.logistic.system.application.dto.request.MomoCallbackRequest;
import com.logistic.system.domain.model.Payment;

public interface MomoService {
    String getPaymentUrl(Payment payment);

    boolean verifyCallbackSignature(MomoCallbackRequest callback);

    Map<String, Object> refund(Long orderId, BigDecimal amount);
}
