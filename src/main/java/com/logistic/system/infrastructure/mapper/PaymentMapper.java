package com.logistic.system.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.logistic.system.application.dto.request.PaymentRequest;
import com.logistic.system.application.dto.response.PaymentResponse;
import com.logistic.system.domain.model.Payment;
import com.logistic.system.infrastructure.persistence.entity.OrderEntity;
import com.logistic.system.infrastructure.persistence.entity.PaymentEntity;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    // Giai đoạn 1: Lúc mới tạo (Lấy từ 2 nguồn vì chưa có bản ghi Payment trong DB)
    @Mapping(target = "orderId", source = "order.orderId")
    @Mapping(target = "amount", source = "order.totalAmount")
    @Mapping(target = "paymentMethod", source = "request.paymentMethod")
    // Bỏ qua các trường chưa có lúc khởi tạo
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "payUrl", ignore = true)
    @Mapping(target = "rawResponse", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    @Mapping(target = "expiredAt", ignore = true)
    @Mapping(target = "paymentStatus", constant = "PENDING")
    Payment toDomain(OrderEntity order, PaymentRequest request);

    /**
     * Chuyển từ Domain Object sang Entity để lưu xuống Database
     */
    @Mapping(target = "order.orderId", source = "orderId") // Map ngược ID về quan hệ Order
    @Mapping(target = "transId", source = "transactionId") // Map transactionId sang transId của Entity
    @Mapping(target = "paymentStatus", source = "paymentStatus")
    @Mapping(target = "requestId", source = "requestId")
    // Nếu các trường khác như amount, payUrl, expiredAt trùng tên thì MapStruct tự
    // map
    // Bỏ qua các trường không có trong Domain hoặc tự động sinh
    @Mapping(target = "currency", constant = "VND") // Thường MoMo dùng VND, Thức có thể gán cứng
    @Mapping(target = "updatedAt", ignore = true) // Để JPA tự lo hoặc Hibernate lo
    @Mapping(target = "paymentId", ignore = true) // ID tự tăng, không map từ domain
    PaymentEntity toEntity(Payment domain);

    // Chuyển từ Entity (DB) sang Domain (Nghiệp vụ)
    @Mapping(target = "orderId", source = "order.orderId")
    @Mapping(target = "transactionId", source = "transId")
    Payment toDomain(PaymentEntity entity);

    // Chuyển từ Domain sang Response (Trả về cho Client)
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "paymentCode", source = "transactionId")
    @Mapping(target = "status", source = "paymentStatus")
    @Mapping(target = "statusLabel", expression = "java(domain.getPaymentStatus().getLabel())") // Lấy tiếng Việt
    PaymentResponse toResponse(Payment domain);

    // Chuyển từ Entity sang Response (Dùng nhanh ở Application Layer)
    @Mapping(target = "orderId", source = "order.orderId")
    @Mapping(target = "paymentCode", source = "transId")
    @Mapping(target = "status", source = "paymentStatus")
    @Mapping(target = "statusLabel", expression = "java(entity.getPaymentStatus().getLabel())")
    PaymentResponse entityToResponse(PaymentEntity entity);
}