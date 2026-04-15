package com.logistic.system.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
public class Payment {
    private Long paymentId;
    private String requestId;
    private Long orderId;
    private String transactionId;
    private BigDecimal amount;
    private PaymentMethods paymentMethod;
    private PaymentStatus paymentStatus;
    private String payUrl; // Link thanh toán để khách quay lại nếu cần
    private String rawResponse; // Lưu toàn bộ JSON phản hồi từ MoMo (dạng Text) để đối soát

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime expiredAt;

    // getter and setter
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethods getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethods paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    // getter and setter
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    // Business Logic: Kiểm tra xem thanh toán còn hạn không
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    /**
     * Hàm chuẩn bị dữ liệu kỹ thuật
     */
    public void prepareTechnicalDetails() {
        // 1. Sinh requestId duy nhất để MoMo định danh phiên thanh toán này
        this.requestId = UUID.randomUUID().toString();

        // 2. Thiết lập thời gian hết hạn (ví dụ: 15 phút từ lúc bấm nút)
        this.expiredAt = LocalDateTime.now().plusMinutes(15);

        // 3. Đảm bảo trạng thái ban đầu luôn là PENDING
        this.paymentStatus = PaymentStatus.PENDING;
    }

    /**
     * Hàm cập nhật trạng thái thanh toán
     * phòng thêm trướng hợp callback lần 2 bị fail
     */
    public void updateStatus(PaymentStatus newStatus) {
        // Thức có thể thêm logic kiểm tra tại đây
        if (this.paymentStatus == PaymentStatus.SUCCESS) {
            return; // Đã thành công rồi thì không cập nhật lại nữa để tránh lỗi dữ liệu
        }

        this.paymentStatus = newStatus;

        // Nếu trạng thái mới là SUCCESS, Thức có thể gán luôn ngày thanh toán
        if (newStatus == PaymentStatus.SUCCESS) {
            this.paidAt = LocalDateTime.now();
        }
    }
}
