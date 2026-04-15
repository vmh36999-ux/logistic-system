package com.logistic.system.interfaces.dto.response;

public interface InventoryResponse {
    // Spring sẽ tự gọi getProductId() để lấy giá trị từ cột product_id trong DB
    Long getProductId();

    // Alias trong query phải khớp với tên hàm này (ví dụ: SELECT quantity AS
    // availableQuantity...)
    Integer getAvailableQuantity();

    // Bạn có thể lấy dữ liệu từ bảng liên kết (Product)
    String getProductName();

    String getWarehouseName();
}
