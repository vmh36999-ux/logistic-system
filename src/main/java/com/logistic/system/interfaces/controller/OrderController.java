package com.logistic.system.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.reponse.OrderResponse;
import com.logistic.system.application.dto.request.OrderRequest;
import com.logistic.system.application.service.OrderApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    /**
     * API tạo đơn hàng mới
     */
    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        OrderResponse response = orderApplicationService.placeOrder(request.getAccountId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * API lấy thông tin đơn hàng theo orderId
     */
    @GetMapping("/{orderCode}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderCode) {
        OrderResponse response = orderApplicationService.getOrderInfo(orderCode);
        return ResponseEntity.ok(response);
    }
}
