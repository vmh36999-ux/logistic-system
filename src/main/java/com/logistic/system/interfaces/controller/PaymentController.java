package com.logistic.system.interfaces.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.request.MomoCallbackRequest;
import com.logistic.system.application.dto.request.PaymentRequest;
import com.logistic.system.application.dto.response.PaymentResponse;
import com.logistic.system.application.service.PaymentApplicationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    /**
     * Endpoint 1: Tạo yêu cầu thanh toán.
     * Trả về payUrl để Frontend/Mobile redirect khách sang MoMo.
     */
    @PostMapping("/momo/create")
    public ResponseEntity<PaymentResponse> createMomoPayment(@RequestBody PaymentRequest request) {
        log.info(">>> [MOMO CREATE] Bắt đầu tạo thanh toán cho đơn hàng: {}", request.getOrderId());
        PaymentResponse response = paymentApplicationService.createPayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint 2: Redirect URL (Callback)
     * Đây là nơi MoMo dẫn người dùng quay lại Website/App sau khi thanh toán xong.
     * Thường là phương thức GET.
     */
    @GetMapping("/momo-callback")
    public ResponseEntity<String> handleMomoRedirect(@RequestParam Map<String, String> allParams) {
        log.info(">>> [MOMO REDIRECT] Người dùng quay lại hệ thống với params: {}", allParams);
        // Ở đây bạn có thể redirect khách hàng về trang "Thanh toán thành công" của

        return ResponseEntity.ok("Xác nhận giao dịch thành công. Bạn có thể đóng cửa sổ này.");
    }

    /**
     * Endpoint 3: Instant Payment Notification (IPN)
     * MoMo gọi Server-to-Server ngầm qua POST.
     * Đây là nơi QUAN TRỌNG NHẤT để cập nhật trạng thái đơn hàng vào DB.
     */
    @PostMapping("/momo-ipn")
    public ResponseEntity<Void> handleMomoIPN(@RequestBody MomoCallbackRequest callback) {
        log.info(">>> [MOMO IPN] Nhận thông báo từ Server MoMo cho OrderId: {}, Status: {}",
                callback.getOrderId(), callback.getResultCode());

        try {
            paymentApplicationService.handleCallback(callback);
            // MoMo yêu cầu phản hồi HTTP 204 hoặc 200 để xác nhận đã nhận được IPN thành
            // công
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error(">>> [MOMO IPN ERROR] Lỗi xử lý cập nhật đơn hàng: {}", e.getMessage());
            // Trả về lỗi để MoMo có thể thực hiện retry (nếu cần)
            return ResponseEntity.internalServerError().build();
        }
    }
}