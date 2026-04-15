package com.logistic.system.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.domain.model.OrderItem;
import com.logistic.system.domain.service.OrderItemDomainService;
import com.logistic.system.infrastructure.mapper.OrderMapper;
import com.logistic.system.infrastructure.persistence.repository.OrderItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemApplicationService {

    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemDomainService orderItemDomainService;

    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItemById(Long orderItemId) {
        return orderItemRepository.findByOrder_OrderId(orderItemId)
                .stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderItem saveOrderItem(OrderItem orderItem) {
        // Validate nghiệp vụ trước khi lưu
        orderItemDomainService.validate(orderItem);

        var entity = orderMapper.toEntity(orderItem);
        var saved = orderItemRepository.save(entity);
        return orderMapper.toDomain(saved);
    }
}
