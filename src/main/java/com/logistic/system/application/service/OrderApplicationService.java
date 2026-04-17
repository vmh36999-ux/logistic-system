package com.logistic.system.application.service;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.application.dto.request.OrderRequest;
import com.logistic.system.application.dto.response.OrderResponse;
import com.logistic.system.domain.enums.OrderStatus;
import com.logistic.system.domain.enums.PaymentMethods;
import com.logistic.system.domain.enums.PaymentStatus;
import com.logistic.system.domain.enums.Region;
import com.logistic.system.domain.model.Account;
import com.logistic.system.domain.model.Customer;
import com.logistic.system.domain.model.Order;
import com.logistic.system.domain.model.OrderItem;
import com.logistic.system.domain.service.CustomerDomainService;
import com.logistic.system.domain.service.InventoryDomainService;
import com.logistic.system.domain.service.OrderDomainService;
import com.logistic.system.domain.service.ShippingFeeDomainService;
import com.logistic.system.infrastructure.mapper.AccountMapper;
import com.logistic.system.infrastructure.mapper.CustomerMapper;
import com.logistic.system.infrastructure.mapper.OrderMapper;
import com.logistic.system.infrastructure.persistence.entity.CustomerEntity;
import com.logistic.system.infrastructure.persistence.entity.InventoryEntity;
import com.logistic.system.infrastructure.persistence.entity.OrderEntity;
import com.logistic.system.infrastructure.persistence.repository.CustomerRepository;
import com.logistic.system.infrastructure.persistence.repository.InventoryRepository;
import com.logistic.system.infrastructure.persistence.repository.OrderRepository;
import com.logistic.system.infrastructure.persistence.repository.ProductRepository;
import com.logistic.system.infrastructure.persistence.repository.ProvinceRepository;
import com.logistic.system.infrastructure.persistence.repository.WarehouseRepository;
import com.logistic.system.infrastructure.service.MomoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Dùng cái này của Lombok để thay thế Constructor dài dòng
public class OrderApplicationService {

        private final CustomerRepository customerRepository;
        private final OrderRepository orderRepository;
        private final ProductRepository productRepository; // Thêm cái này để lấy giá thật
        private final InventoryRepository inventoryRepository;
        private final ProvinceRepository provinceRepository;
        private final WarehouseRepository warehouseRepository;
        private final OrderMapper orderMapper;
        private final CustomerMapper customerMapper;
        private final AccountMapper accountMapper;
        private final CustomerDomainService customerDomainService;
        private final OrderDomainService orderDomainService;
        private final ShippingFeeDomainService shippingFeeDomainService;
        private final MomoService momoService;
        private final InventoryDomainService inventoryDomainService;

        @Transactional // Rất quan trọng: Đảm bảo nếu lưu OrderItem lỗi thì Order cũng không được tạo
        public OrderResponse placeOrder(Long accountId, OrderRequest request) {
                // 1. Tìm thông tin khách hàng
                CustomerEntity customerEntity = customerRepository.findByAccount_AccountId(accountId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng!"));

                // 2. Chuyển sang Domain để check
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
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Sản phẩm ID " + i.getProductId() + " không tồn tại"));
                        // Kiểm tra tồn kho
                        InventoryEntity inventory = inventoryRepository.findByProduct_ProductId(product.getProductId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Sản phẩm " + product.getName() + " không có trong kho"));

                        if (inventory.getQuantity() < i.getQuantity()) {
                                throw new RuntimeException("Sản phẩm " + product.getName()
                                                + " đã hết hàng hoặc không đủ số lượng");
                        }

                        OrderItem item = new OrderItem();
                        item.setProductId(product.getProductId());
                        item.setProductName(product.getName());
                        item.setQuantity(i.getQuantity());
                        item.setPriceAtPurchase(product.getBasePrice()); // Lấy giá 30tr từ DB
                        item.setWeightGram(product.getWeightGram());
                        item.setNote(i.getNote());
                        return item;
                }).toList());

                // 4. Tính toán phí vận chuyển (Giả định lấy kho đầu tiên làm gốc)
                BigDecimal totalWeightKg = order.getItems().stream()
                                .map(item -> item.getWeightGram().multiply(BigDecimal.valueOf(item.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .divide(BigDecimal.valueOf(1000), 2, java.math.RoundingMode.HALF_UP);

                var destProvince = provinceRepository.findById(order.getReceiverProvinceId())
                                .orElseThrow(() -> new RuntimeException("Tỉnh/Thành người nhận không hợp lệ"));
                Region destReg = Region.fromString(destProvince.getRegion());

                // Lấy một kho mặc định (hoặc kho đầu tiên) để tính phí tạm tính
                var defaultWarehouse = warehouseRepository.findAll().stream().findFirst()
                                .orElseThrow(() -> new RuntimeException("Hệ thống chưa cấu hình kho bãi!"));
                Region sourceReg = Region.fromString(defaultWarehouse.getProvince().getRegion());

                BigDecimal shippingFee = shippingFeeDomainService.calculateShippingFee(sourceReg, destReg,
                                totalWeightKg);
                order.setShippingFee(shippingFee);
                // Kiểm tra trạng thái trừ kho

                // 5. Tính toán tổng tiền và Validate logic nghiệp vụ (ví dụ: tổng tiền > 0)
                orderDomainService.calculateTotal(order);
                orderDomainService.validateOrder(order);

                // 6. Lưu xuống DB
                orderRepository.save(orderMapper.toEntity(order));

                // 7. Trả về Response
                return orderMapper.toResponse(order);
        }

        @Transactional(readOnly = true)
        public OrderResponse getOrderInfo(String orderCode) {
                var entity = orderRepository.findByOrderCode(orderCode)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));
                return orderMapper.toResponse(orderMapper.toDomainFromEntity(entity));
        }

        /**
         * Hủy đơn hàng và thực hiện hoàn tiền nếu đã thanh toán qua MoMo.
         * Đồng thời hoàn trả lại số lượng sản phẩm vào kho.
         */
        @Transactional
        public void cancelAndRefundOrder(Long orderId) {
                // 1. Tìm đơn hàng
                OrderEntity order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + orderId));

                // 2. Kiểm tra điều kiện hủy (Chỉ cho phép hủy nếu chưa giao hàng)
                if (order.getOrderStatus() == OrderStatus.CANCELLED) {
                        throw new RuntimeException("Đơn hàng đã được hủy trước đó.");
                }
                // Bạn có thể thêm điều kiện check ShipmentStatus ở đây nếu cần

                // 3. Xử lý hoàn tiền nếu đã thanh toán qua MoMo
                if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
                        Map<String, Object> refundResult = momoService.refund(orderId, order.getTotalAmount());

                        Object resultCodeObj = refundResult.get("resultCode");
                        int resultCode = (resultCodeObj instanceof Integer) ? (int) resultCodeObj
                                        : Integer.parseInt(resultCodeObj.toString());

                        if (resultCode == 0) {
                                order.setPaymentStatus(PaymentStatus.REFUNDED);
                        } else {
                                String message = (String) refundResult.get("message");
                                throw new RuntimeException("Hoàn tiền thất bại: " + message);
                        }
                }

                // 4. Cập nhật trạng thái đơn hàng
                order.setOrderStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);

                // 5. Hoàn trả tồn kho
                order.getItems().forEach(item -> {
                        inventoryDomainService.increaseStock(item.getProduct().getProductId(), item.getQuantity());
                });
        }

        @Transactional
        public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
                OrderEntity entity = orderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

                Order orderDomain = orderMapper.toDomainFromEntity(entity);

                if (newStatus.equals(OrderStatus.CONFIRMED)) {
                        orderDomainService.confirm(orderDomain);
                        // 3. Thực hiện trừ kho
                        orderDomain.getItems().forEach(item -> {
                                inventoryDomainService.decreaseStock(item.getProductId(), item.getQuantity());
                        });
                }

                entity.setOrderStatus(newStatus);
                orderRepository.save(entity);
        }
}