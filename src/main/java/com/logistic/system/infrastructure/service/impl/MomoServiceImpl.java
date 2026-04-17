package com.logistic.system.infrastructure.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.logistic.system.application.dto.request.MomoCallbackRequest;
import com.logistic.system.domain.model.Payment;
import com.logistic.system.infrastructure.persistence.entity.PaymentEntity;
import com.logistic.system.infrastructure.persistence.repository.PaymentRepository;
import com.logistic.system.infrastructure.security.MomoSignatureUtils;
import com.logistic.system.infrastructure.service.MomoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomoServiceImpl implements MomoService {

    @Value("${payment.momo.partner-code}")
    private String partnerCode;

    @Value("${payment.momo.access-key}")
    private String accessKey;

    @Value("${payment.momo.secret-key}")
    private String secretKey;

    @Value("${payment.momo.endpoint}")
    private String endpoint;

    @Value("${payment.momo.return-url}")
    private String returnUrl;

    @Value("${payment.momo.notify-url}")
    private String notifyUrl;

    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;

    @Override
    public String getPaymentUrl(Payment payment) {
        String orderInfo = "Thanh toan don hang " + payment.getOrderId();
        String requestId = payment.getRequestId();
        String orderId = payment.getRequestId(); // MoMo yêu cầu orderId duy nhất
        String amount = payment.getAmount().toBigInteger().toString();
        String extraData = "";
        String requestType = "captureWallet";

        // Trim các giá trị cấu hình để tránh khoảng trắng thừa
        String pCode = partnerCode.trim();
        String iUrl = notifyUrl.trim();
        String rUrl = returnUrl.trim();

        // 1. Tạo chuỗi ký tự theo quy tắc của MoMo v2 (Sắp xếp Alphabet)
        // KHÔNG bao gồm accessKey trong chuỗi ký của yêu cầu tạo thanh toán v2
        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + (extraData == null ? "" : extraData) +
                "&ipnUrl=" + iUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + pCode +
                "&redirectUrl=" + rUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        // 2. Ký HMAC-SHA256
        String signature = MomoSignatureUtils.generateSignature(rawSignature, secretKey.trim());

        Map<String, Object> body = new HashMap<>();
        body.put("partnerCode", pCode);
        body.put("requestId", requestId);
        body.put("amount", amount);
        body.put("orderId", orderId);
        body.put("orderInfo", orderInfo);
        body.put("redirectUrl", rUrl);
        body.put("ipnUrl", iUrl);
        body.put("extraData", ""); // Đảm bảo là chuỗi rỗng fix cứng luôn
        body.put("requestType", requestType);
        body.put("signature", signature);
        body.put("lang", "vi");

        // 4. Gọi API MoMo
        try {
            log.info("Sending request to MoMo: {}", body);
            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint.trim(), body, Map.class);
            log.info("MoMo response: {}", response.getBody());

            if (response.getBody() != null) {
                Object resultCodeObj = response.getBody().get("resultCode");
                int resultCode = (resultCodeObj instanceof Integer) ? (int) resultCodeObj
                        : Integer.parseInt(resultCodeObj.toString());

                if (resultCode == 0) {
                    return response.getBody().get("payUrl").toString();
                } else {
                    String message = (String) response.getBody().get("message");
                    log.error("MoMo error: {} - {}", resultCode, message);
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi gọi API MoMo: {}", e.getMessage());
            throw new RuntimeException("Kết nối ví MoMo thất bại: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean verifyCallbackSignature(MomoCallbackRequest callback) {
        try {
            // Chuỗi dữ liệu raw data để kiểm tra chữ ký, phải sắp xếp alphabet
            String rawData = "accessKey=" + accessKey +
                    "&amount=" + callback.getAmount() +
                    "&extraData=" + callback.getExtraData() +
                    "&message=" + callback.getMessage() +
                    "&orderId=" + callback.getOrderId() +
                    "&orderInfo=" + callback.getOrderInfo() +
                    "&orderType=" + callback.getOrderType() +
                    "&partnerCode=" + callback.getPartnerCode() +
                    "&payType=" + callback.getPayType() +
                    "&requestId=" + callback.getRequestId() +
                    "&responseTime=" + callback.getResponseTime() +
                    "&resultCode=" + callback.getResultCode() +
                    "&transId=" + callback.getTransId();

            String calculatedSignature = MomoSignatureUtils.generateSignature(rawData, secretKey);
            return calculatedSignature.equals(callback.getSignature());
        } catch (Exception e) {
            log.error("Exception when verifying MoMo signature", e);
            return false;
        }
    }

    public Map<String, Object> refund(Long orderId, BigDecimal amount) {
        // 1. Tìm thông tin thanh toán gốc (transId)
        PaymentEntity payment = paymentRepository.findFirstByOrder_OrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(
                        () -> new RuntimeException("Không tìm thấy thông tin thanh toán cho đơn hàng: " + orderId));

        if (payment.getTransId() == null) {
            throw new RuntimeException("Giao dịch chưa có transId, không thể hoàn tiền.");
        }

        String requestId = UUID.randomUUID().toString();
        String orderIdStr = "REFUND-" + orderId + "-" + System.currentTimeMillis();
        String description = "Hoan tien don hang " + orderId;

        // 2. Tạo chuỗi ký tự theo quy tắc của MoMo Refund v2 (Sắp xếp Alphabet)
        // accessKey=$accessKey&amount=$amount&description=$description&orderId=$orderId&partnerCode=$partnerCode&requestId=$requestId&transId=$transId
        String rawSignature = "accessKey=" + accessKey.trim() +
                "&amount=" + amount.toBigInteger().toString() +
                "&description=" + description +
                "&orderId=" + orderIdStr +
                "&partnerCode=" + partnerCode.trim() +
                "&requestId=" + requestId +
                "&transId=" + payment.getTransId();

        // 3. Ký HMAC-SHA256
        String signature = MomoSignatureUtils.generateSignature(rawSignature, secretKey.trim());

        // 4. Tạo Body Request
        Map<String, Object> body = new HashMap<>();
        body.put("partnerCode", partnerCode.trim());
        body.put("requestId", requestId);
        body.put("orderId", orderIdStr);
        body.put("amount", amount.toBigInteger().toString());
        body.put("transId", payment.getTransId());
        body.put("description", description);
        body.put("signature", signature);
        body.put("lang", "vi");

        // 5. Gọi API Refund của MoMo
        try {
            String refundUrl = endpoint.trim().replace("/create", "/refund");
            log.info("Sending Refund request to MoMo: {}", body);
            ResponseEntity<Map> response = restTemplate.postForEntity(refundUrl, body, Map.class);
            log.info("MoMo Refund response: {}", response.getBody());

            return response.getBody();
        } catch (Exception e) {
            log.error("Lỗi khi gọi API Refund MoMo: {}", e.getMessage());
            throw new RuntimeException("Hoàn tiền qua MoMo thất bại: " + e.getMessage());
        }
    }
}
