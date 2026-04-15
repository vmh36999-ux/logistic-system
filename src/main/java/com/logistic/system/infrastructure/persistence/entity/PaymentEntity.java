package com.logistic.system.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.logistic.system.domain.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PaymentEntity đóng vai trò là Audit Log cho các giao dịch tài chính.
 * Lưu trữ thông tin định danh từ MoMo để đối soát khi cần thiết.
 */
@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_request_id", columnList = "request_id"),
        @Index(name = "idx_payment_trans_id", columnList = "trans_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 10)
    @Builder.Default
    private String currency = "VND";

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    /**
     * requestId: Mã duy nhất cho mỗi yêu cầu gửi sang MoMo.
     * Dùng để chống trùng lặp (Idempotency).
     */
    @Column(name = "request_id", unique = true, length = 100)
    private String requestId;

    /**
     * transId: Mã giao dịch do MoMo trả về sau khi thành công.
     */
    @Column(name = "trans_id", length = 100)
    private String transId;

    @Column(name = "pay_url", length = 500)
    private String payUrl;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * rawResponse: Lưu toàn bộ JSON MoMo gửi về (IPN/Callback) để Audit.
     */
    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ... các trường cũ giữ nguyên ...

    @Column(name = "paid_at")
    private LocalDateTime paidAt; // Thời điểm MoMo xác nhận thành công

    @Column(name = "expired_at")
    private LocalDateTime expiredAt; // Thời điểm hết hạn link thanh toán

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
