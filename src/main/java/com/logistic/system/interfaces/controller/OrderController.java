package com.logistic.system.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.request.OrderRequest;
import com.logistic.system.application.dto.response.OrderResponse;
import com.logistic.system.application.service.OrderApplicationService;
import com.logistic.system.domain.enums.OrderStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Các API xử lý quy trình đặt hàng và truy vấn đơn hàng")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    @Operation(summary = "Đặt hàng mới", description = "Xử lý quy trình tạo đơn hàng mới cho khách hàng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đặt hàng thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc sản phẩm hết hàng"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực")
    })
    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderApplicationService.placeOrder(request.getAccountId(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Truy vấn đơn hàng", description = "Lấy thông tin chi tiết của đơn hàng thông qua mã đơn hàng (Order Code)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy thông tin đơn hàng"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy mã đơn hàng này")
    })
    @GetMapping("/{orderCode}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderCode) {
        OrderResponse response = orderApplicationService.getOrderInfo(orderCode);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Hủy đơn hàng và hoàn tiền", description = "Thực hiện hoàn tiền MoMo (nếu có) và trả lại hàng vào kho")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hủy và hoàn tiền thành công"),
            @ApiResponse(responseCode = "400", description = "Lỗi nghiệp vụ hoặc cổng thanh toán")
    })
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        orderApplicationService.cancelAndRefundOrder(orderId);
        return ResponseEntity.ok("Đơn hàng đã được hủy và thực hiện hoàn tiền thành công.");
    }

    @PutMapping("/{orderId}/updateStatus")
    @Operation(summary = "Cập nhật trạng thái đơn hàng", description = "Cập nhật trạng thái đơn hàng theo mã đơn hàng (Order Code)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatus newStatus) {
        orderApplicationService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok("Đơn hàng đã cập nhật thành công.");
    }
}