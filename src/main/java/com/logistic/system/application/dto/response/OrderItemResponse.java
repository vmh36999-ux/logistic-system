package com.logistic.system.application.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    // private Long orderId;
    private Long productId;

    // Thêm các thông tin hiển thị (thường lấy từ Domain sau khi map từ Entity)
    private String productName;

    private Integer quantity;

    // Giá tại thời điểm mua (Price at purchase)
    private BigDecimal priceAtPurchase;

    // Thành tiền cho từng item (Thường là quantity * priceAtPurchase)
    private BigDecimal subTotal;

    private String note;
}
