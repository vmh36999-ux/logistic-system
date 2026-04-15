# Quy trình Kiểm thử Hệ thống (Testing v1.0)

Để đảm bảo chất lượng, quy trình kiểm thử sẽ tập trung vào các kịch bản thực tế của hệ thống Logistics.

## 1. Kiểm thử Đơn vị (Unit Testing)
- **Shipping Fee**: Test ma trận vùng miền trong [ShippingFeeDomainService.java](file:///d:/SE194498/system/domain/service/ShippingFeeDomainService.java) với các trường hợp:
    - Cùng miền (Nội vùng).
    - Bắc - Nam (Liên vùng xa).
    - Cân nặng vượt mức (ví dụ: 1.2kg, 2.5kg).
- **Status Transition**: Test các quy tắc chuyển trạng thái trong [ShipmentTrackingDomainService.java](file:///d:/SE194498/system/domain/service/ShipmentTrackingDomainService.java) (ví dụ: Đang giao không thể nhảy ngược về Chờ xử lý).

## 2. Kiểm thử Tích hợp (Integration Testing)
- **Flow Đặt hàng**: 
    1. Tạo đơn hàng ([OrderApplicationService.java](file:///d:/SE194498/system/application/service/OrderApplicationService.java)).
    2. Tạo vận đơn ([ShipmentApplicationService.java](file:///d:/SE194498/system/application/service/ShipmentApplicationService.java)).
    3. Kiểm tra phí vận chuyển lưu trong DB có đúng với logic phân vùng không.
- **Flow Giao hàng**:
    1. Ghi nhận lần giao 1 (Thất bại).
    2. Ghi nhận lần giao 2 (Thất bại).
    3. Ghi nhận lần giao 3 (Thất bại) -> Kiểm tra `Shipment` có tự chuyển sang `RETURNED` không.

## 3. Kiểm thử Dữ liệu (Data Integrity)
- Kiểm tra việc chuẩn hóa vùng miền qua Enum `Region`. Test với dữ liệu DB "Miền Bắc", "bắc", "North" xem có nhận diện đúng không.
- Kiểm tra tính toàn vẹn khi lưu Log tracking: Mỗi lần đổi trạng thái phải có 1 bản ghi log tương ứng.

## 4. Kiểm thử Hiệu năng (Performance - Cơ bản)
- Kiểm tra thời gian phản hồi của API lấy Timeline vận đơn khi có hàng trăm bản ghi log.

## 5. Kiểm thử với Postman (Manual Testing)

Dưới đây là các kịch bản quan trọng cần test trên Postman:

### Kịch bản 1: Tạo Đơn hàng (Place Order)
- **Method**: `POST`
- **URL**: `{{base_url}}/api/orders/place`
- **Body (JSON)**:
```json
{
    "accountId": 1,
    "items": [
        { "productId": 101, "quantity": 2 },
        { "productId": 102, "quantity": 1 }
    ],
    "receiverName": "Nguyen Van A",
    "receiverPhone": "0901234567",
    "receiverProvinceId": 1,
    "receiverAddress": "123 Đường ABC, Quận 1"
}
```
- **Mong đợi**: Trả về `OrderResponse` với `totalAmount` được tính toán đúng.

### Kịch bản 2: Tạo Vận đơn & Tính phí vùng miền (Create Shipment)
- **Method**: `POST`
- **URL**: `{{base_url}}/api/shipments`
- **Body (JSON)**:
```json
{
    "orderId": 1,
    "warehouseId": 10,
    "totalWeightGram": 1500,
    "receiverProvinceId": 2
}
```
- **Mong đợi**: `shippingFee` được tính dựa trên logic: `Nội vùng (15k) / Liên vùng gần (30k) / Liên vùng xa (50k)` + phí vượt cân (nếu > 1kg).

### Kịch bản 3: Ghi nhận lần giao hàng (Record Delivery Attempt)
- **Method**: `POST`
- **URL**: `{{base_url}}/api/delivery/attempts`
- **Body (JSON)**:
```json
{
    "shipmentId": 1,
    "status": "FAILED",
    "reason": "Khách hàng không nghe máy",
    "note": "Giao lần 1"
}
```
- **Mong đợi**: 
    - Tạo 1 record trong `delivery_attempts`.
    - Trạng thái `Shipment` giữ nguyên là `PICKED_UP` (hoặc `OUT_FOR_DELIVERY`).
    - Nếu gửi 3 lần `FAILED` liên tiếp -> Trạng thái `Shipment` tự động chuyển sang `RETURNED`.

### Kịch bản 4: Xem lịch sử vận đơn (Tracking Timeline)
- **Method**: `GET`
- **URL**: `{{base_url}}/api/shipments/{id}/timeline`
- **Mong đợi**: Danh sách các bước thay đổi trạng thái kèm thời gian và mô tả chi tiết.

---
*Mục tiêu: Đạt độ bao phủ (Coverage) ít nhất 80% logic nghiệp vụ cốt lõi.*
