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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Management", description = "Các API xử lý thanh toán qua ví điện tử (MoMo)")
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    @Operation(summary = "Tạo thanh toán MoMo", description = "Khởi tạo giao dịch thanh toán và lấy đường dẫn redirect sang MoMo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo yêu cầu thanh toán thành công"),
            @ApiResponse(responseCode = "400", description = "Đơn hàng không hợp lệ hoặc đã thanh toán")
    })
    @PostMapping("/momo/create")
    public ResponseEntity<PaymentResponse> createMomoPayment(@Valid @RequestBody PaymentRequest request) {
        log.info(">>> [MOMO CREATE] Bắt đầu tạo thanh toán cho đơn hàng: {}", request.getOrderId());
        PaymentResponse response = paymentApplicationService.createPayment(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xử lý Redirect từ MoMo", description = "Tiếp nhận người dùng quay lại từ trang thanh toán MoMo")
    @GetMapping("/momo-callback")
    public ResponseEntity<String> handleMomoRedirect(@RequestParam Map<String, String> allParams) {
        log.info(">>> [MOMO REDIRECT] Người dùng quay lại hệ thống với params: {}", allParams);
        return ResponseEntity.ok("Xác nhận giao dịch thành công. Bạn có thể đóng cửa sổ này.");
    }

    @Operation(summary = "Nhận thông báo IPN từ MoMo", description = "API dành riêng cho Server MoMo gọi ngầm để cập nhật trạng thái thanh toán")
    @PostMapping("/momo-ipn")
    public ResponseEntity<Void> handleMomoIPN(@RequestBody MomoCallbackRequest callback) {
        log.info(">>> [MOMO IPN] Nhận thông báo từ Server MoMo cho OrderId: {}, Status: {}",
                callback.getOrderId(), callback.getResultCode());

        try {
            paymentApplicationService.handleCallback(callback);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error(">>> [MOMO IPN ERROR] Lỗi xử lý cập nhật đơn hàng: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}