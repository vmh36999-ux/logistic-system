package com.logistic.system.domain.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.enums.PaymentStatus;
import com.logistic.system.domain.model.Payment;

@Service
public class PaymentDomainService {

    /**
     * KIỂM TRA TRƯỚC KHI KHỞI TẠO (Dùng cho hàm createPayment)
     */
    public void validateEligibility(Payment payment) {
        // 1. Số tiền phải hợp lệ
        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền thanh toán phải lớn hơn 0");
        }

        // 2. Kiểm tra trạng thái đơn hàng (logic này có thể gọi qua Order domain)
        // Ví dụ: Order must be PENDING_PAYMENT
    }

    /**
     * KIỂM TRA KHI CÓ CALLBACK (Dùng cho hàm handleCallback)
     */
    public void validatePaymentSuccess(Payment payment, BigDecimal callbackAmount) {
        // 1. Kiểm tra số tiền: Phải khớp 100% với số tiền lúc tạo link
        if (payment.getAmount().compareTo(callbackAmount) != 0) {
            throw new IllegalArgumentException("Số tiền thanh toán bị sai lệch (Security Risk!)");
        }

        // 2. Kiểm tra trạng thái: Chỉ xử lý nếu đang PENDING
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            // Trường hợp này thường do MoMo gọi lại lần 2, mình nên ignore hoặc báo success
            // chứ không nên ném lỗi làm MoMo gọi retry mãi.
            return;
        }

        // 3. Kiểm tra hết hạn (Dùng thời gian lúc hệ thống nhận callback)
        if (payment.isExpired()) {
            throw new IllegalArgumentException("Giao dịch đã quá hạn 15 phút!");
        }
    }

    /**
     * PHÂN LOẠI MÃ LỖI MOMO
     */
    public PaymentStatus resolveStatusFromCode(String resultCode) {
        if ("0".equals(resultCode)) {
            return PaymentStatus.SUCCESS;
        }
        // Có thể map thêm: 1006 -> CANCELLED_BY_USER, 9000 -> SYSTEM_ERROR
        return PaymentStatus.FAILED;
    }
}