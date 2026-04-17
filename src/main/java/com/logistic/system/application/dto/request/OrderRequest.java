package com.logistic.system.application.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotNull(message = "Tài khoản không được để trống")
    private Long accountId;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String receiverName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    private String receiverPhone;

    @NotNull(message = "Tỉnh/Thành không được để trống")
    private Long receiverProvinceId;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String receiverAddress;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;

    private String note;

    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    private List<OrderItemRequest> items;
}
