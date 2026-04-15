package com.logistic.system.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.application.dto.reponse.OrderResponse;
import com.logistic.system.application.dto.request.OrderRequest;
import com.logistic.system.domain.enums.OrderStatus;
import com.logistic.system.domain.enums.PaymentMethods;
import com.logistic.system.domain.enums.PaymentStatus;
import com.logistic.system.domain.model.Account;
import com.logistic.system.domain.model.Customer;
import com.logistic.system.domain.model.Order;
import com.logistic.system.domain.model.OrderItem;
import com.logistic.system.domain.service.CustomerDomainService;
import com.logistic.system.domain.service.OrderDomainService;
import com.logistic.system.infrastructure.mapper.AccountMapper;
import com.logistic.system.infrastructure.mapper.CustomerMapper;
import com.logistic.system.infrastructure.mapper.OrderMapper;
import com.logistic.system.infrastructure.persistence.entity.CustomerEntity;
import com.logistic.system.infrastructure.persistence.repository.CustomerRepository;
import com.logistic.system.infrastructure.persistence.repository.OrderRepository;
import com.logistic.system.infrastructure.persistence.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Dùng cái này của Lombok để thay thế Constructor dài dòng
public class OrderApplicationService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository; // Thêm cái này để lấy giá thật
    private final OrderMapper orderMapper;
    private final CustomerMapper customerMapper;
    private final AccountMapper accountMapper;
    private final CustomerDomainService customerDomainService;
    private final OrderDomainService orderDomainService;

    @Transactional // Rất quan trọng: Đảm bảo nếu lưu OrderItem lỗi thì Order cũng không được tạo
    public OrderResponse placeOrder(Long accountId, OrderRequest request) {
        // 1. Tìm thông tin khách hàng
        CustomerEntity customerEntity = customerRepository.findByAccount_AccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng!"));

        // 2. Chuyển sang Domain để check "Luật chơi"
        Customer customerDomain = customerMapper.toDomain(customerEntity);
        Account accountDomain = accountMapper.toDomain(customerEntity.getAccount());

        if (!customerDomainService.canPlaceOrder(customerDomain, accountDomain)) {
            throw new RuntimeException("Tài khoản của bạn không đủ quyền hoặc đang bị khóa!");
        }

        // 3. Map request sang Domain và lấy GIÁ THẬT từ DB
        Order order = new Order();
        order.setCustomerId(customerEntity.getCustomerId());

        // Map các thông tin giao hàng từ request
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverProvinceId(request.getReceiverProvinceId());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setPaymentMethod(PaymentMethods.valueOf(request.getPaymentMethod().toUpperCase()));
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setNote(request.getNote());
        order.setItems(request.getItems().stream().map(i -> {
            var product = productRepository.findById(i.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm ID " + i.getProductId() + " không tồn tại"));

            OrderItem item = new OrderItem();
            item.setProductId(product.getProductId());
            item.setQuantity(i.getQuantity());
            item.setPriceAtPurchase(product.getBasePrice()); // Lấy giá 30tr từ DB
            item.setWeightGram(product.getWeightGram());
            item.setNote(i.getNote());
            return item;
        }).toList());

        // 4. Tính toán tổng tiền và Validate logic nghiệp vụ (ví dụ: tổng tiền > 0)
        orderDomainService.calculateTotal(order);
        orderDomainService.validateOrder(order);

        // 5. Lưu xuống DB
        var savedEntity = orderRepository.save(orderMapper.toEntity(order));

        // 6. Trả về Response
        return orderMapper.toResponse(orderMapper.toDomain(savedEntity));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderInfo(String orderCode) {
        var entity = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
        return orderMapper.toResponse(orderMapper.toDomain(entity));
    }
}