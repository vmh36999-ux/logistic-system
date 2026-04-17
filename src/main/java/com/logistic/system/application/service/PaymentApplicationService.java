package com.logistic.system.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.application.dto.request.PaymentRequest;
import com.logistic.system.application.dto.response.PaymentResponse;
import com.logistic.system.domain.enums.OrderStatus;
import com.logistic.system.domain.enums.PaymentStatus;
import com.logistic.system.domain.model.Payment;
import com.logistic.system.domain.service.InventoryDomainService;
import com.logistic.system.domain.service.PaymentDomainService;
import com.logistic.system.infrastructure.mapper.PaymentMapper;
import com.logistic.system.infrastructure.persistence.entity.OrderEntity;
import com.logistic.system.infrastructure.persistence.entity.PaymentEntity;
import com.logistic.system.infrastructure.persistence.repository.OrderRepository;
import com.logistic.system.infrastructure.persistence.repository.PaymentRepository;
import com.logistic.system.infrastructure.service.MomoService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentApplicationService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentDomainService paymentDomainService;
    private final OrderRepository orderRepository;
    private final MomoService momoService; // Infrastructure thực hiện gọi API MoMo;
    private final InventoryDomainService inventoryDomainService; // Domain Service xử lý thao tác kho hàng;

    /**
     * Khởi tạo giao dịch thanh toán từ yêu cầu của khách hàng
     */
    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        // 1. Tìm đơn hàng gốc dựa trên orderId trong PaymentRequest
        OrderEntity orderEntity = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng để thanh toán"));

        // 2. Map sang Domain Object để xử lý nghiệp vụ
        // Mapper sẽ lấy thông tin từ orderEntity và các tùy chọn từ paymentRequest
        Payment paymentDomain = paymentMapper.toDomain(orderEntity, paymentRequest);

        // 3. Điều phối Domain Service kiểm tra logic (số tiền, trạng thái đơn hàng,...)
        paymentDomainService.validateEligibility(paymentDomain);

        // 4. Chuẩn bị dữ liệu kỹ thuật (sinh requestId, thiết lập expiredAt)
        paymentDomain.prepareTechnicalDetails();

        // 5. Gọi Infrastructure Layer (MomoService) để tương tác với phía MoMo
        String payUrl = momoService.getPaymentUrl(paymentDomain);
        paymentDomain.setPayUrl(payUrl);

        // 6. Lưu trạng thái PENDING xuống Persistence Layer
        PaymentEntity paymentEntity = paymentMapper.toEntity(paymentDomain);
        @SuppressWarnings("unused")
        PaymentEntity savedEntity = paymentRepository.save(paymentEntity);

        // 7. Trả về Response cho Client
        return paymentMapper.toResponse(paymentDomain);
    }

    /**
     * Tiếp nhận và xử lý kết quả trả về từ MoMo (IPN/Callback)
     */
    @Transactional
    public void handleCallback(com.logistic.system.application.dto.request.MomoCallbackRequest callback) {
        // 0. Xác thực chữ ký MoMo gửi về (CỰC KỲ QUAN TRỌNG)
        if (!momoService.verifyCallbackSignature(callback)) {
            log.error("Giả mạo chữ ký MoMo! Payload: {}", callback);
            throw new RuntimeException("Chữ ký MoMo không hợp lệ");
        }

        // 1 Truy vấn những giao dịch nào đang chờ xử lý
        // Lưu ý: MoMo trả về orderId chính là requestId mà mình đã sinh ra
        PaymentEntity entity = paymentRepository.findByRequestId(callback.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Giao dịch không tồn tại trên hệ thống"));

        // 2. Chuyển sang Domain để xử lý trạng thái
        Payment paymentDomain = paymentMapper.toDomain(entity);

        // 3. Phân giải resultCode từ MoMo sang PaymentStatus của hệ thống
        PaymentStatus status = paymentDomainService.resolveStatusFromCode(String.valueOf(callback.getResultCode()));

        // 4. Cập nhật trạng thái vào Domain Model
        paymentDomain.updateStatus(status);

        // 5. Đồng bộ kết quả vào Database
        entity.setPaymentStatus(paymentDomain.getPaymentStatus());
        entity.setTransId(String.valueOf(callback.getTransId()));

        // Nếu thành công, cập nhật thêm các mốc thời gian và trạng thái Đơn hàng
        if (paymentDomain.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
            entity.setPaidAt(LocalDateTime.now());
            entity.getOrder().setOrderStatus(OrderStatus.PAID);
            entity.getOrder().setPaymentStatus(PaymentStatus.SUCCESS);
            // entity.getOrder().setShippingFee(BigDecimal.ZERO);
            // entity.getOrder().setTotalAmount(BigDecimal.ZERO);
            if (entity.getOrder().getOrderStatus().equals(OrderStatus.PAID)) {
                entity.getOrder().getItems().forEach(item -> {
                    inventoryDomainService.decreaseStock(item.getProduct().getProductId(),
                            item.getQuantity());

                });
            }
            log.info("Đơn hàng {} đã thanh toán thành công", entity.getOrder().getOrderId());
        }

        paymentRepository.save(entity);
    }
}
