# Kế hoạch Hoàn thiện Dự án (v6.0) - Senior Developer Perspective

Dự án đã hoàn thành các module cốt lõi. Đây là kế hoạch để đưa hệ thống lên mức "Production-Ready".

## 1. Hoàn thiện Logic Nghiệp vụ (Business Logic)

- **Quản lý Kho (Inventory)**: Tích hợp tự động trừ kho khi đơn hàng chuyển sang trạng thái `PICKED_UP`. Hiện tại mới chỉ có hàm lẻ trong `InventoryDomainService`.
- **Thông báo (Notifications)**: Xây dựng module gửi Email/SMS thông báo cho khách hàng khi trạng thái `Shipment` thay đổi (đặc biệt là khi `OUT_FOR_DELIVERY` và `DELIVERED`).
- **Xử lý Hoàn hàng (Returns)**: Hoàn thiện quy trình nhập lại kho khi `Shipment` ở trạng thái `RETURNED`.

## 2. Tối ưu hóa Kiến trúc (Architectural Improvements)

- **Cấu hình Động (Dynamic Config)**: Chuyển các hằng số phí vận chuyển trong [ShippingFeeDomainService.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/domain/service/ShippingFeeDomainService.java) vào Database hoặc file `.yml` để thay đổi không cần sửa code. // Tính sau 
- **Xử lý Exception tập trung**: Xây dựng `@ControllerAdvice` để format lại toàn bộ lỗi trả về cho Client (thay vì quăng `RuntimeException` thô).// Tính sau 
- **Security**: Tích hợp Spring Security và JWT để bảo vệ các API nghiệp vụ, phân quyền rõ ràng giữa `CUSTOMER`, `STAFF`, và `ADMIN`.

## 3. Hiệu năng & Mở rộng (Scalability)

- **Caching**: Áp dụng Redis cho các dữ liệu ít thay đổi như danh sách Tỉnh/Thành ([ProvinceEntity.java](file:///d:/SE194498/system/infrastructure/persistence/entity/ProvinceEntity.java)). // Tính sau 
- **Async Processing**: Sử dụng `@Async` hoặc Message Queue (RabbitMQ/Kafka) cho việc ghi log tracking và gửi thông báo để không làm chậm luồng chính. 

## 4. Tài liệu & Deployment

- **API Documentation**: Tích hợp Swagger/OpenAPI để tự động tạo tài liệu API cho Frontend.
- **Dockerization**: Viết `Dockerfile` và `docker-compose.yml` để đóng gói ứng dụng và DB.

***

*Kế hoạch này tập trung vào tính bền vững và khả năng bảo trì lâu dài của hệ thống.*
