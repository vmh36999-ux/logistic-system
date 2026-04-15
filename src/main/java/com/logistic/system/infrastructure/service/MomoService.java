package com.logistic.system.infrastructure.service;

import com.logistic.system.application.dto.request.MomoCallbackRequest;
import com.logistic.system.domain.model.Payment;

public interface MomoService {
    String getPaymentUrl(Payment payment);

    boolean verifyCallbackSignature(MomoCallbackRequest callback);
}
