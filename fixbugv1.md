# Báo cáo Fix Bug & Tối ưu hóa Hệ thống (v1.0) - Senior Developer

Sau khi rà soát toàn bộ dự án, tôi đã xác định được một số vấn đề nghiêm trọng về mặt kỹ thuật và kiến trúc có thể gây lỗi khi vận hành. Dưới đây là danh sách các lỗi và hướng xử lý đã thực hiện.

## 1. Lỗi Nghiệp vụ & Logic (Critical)

### 1.1. Cập nhật trạng thái Shipment (DeliveryAttemptApplicationService)
- **Vấn đề**: Trong hàm `recordAttempt`, hệ thống chỉ lưu bản ghi `DeliveryAttemptEntity` mà **KHÔNG** cập nhật trạng thái mới của `ShipmentEntity` vào Database. Điều này dẫn đến việc đơn hàng luôn ở trạng thái cũ (ví dụ: `OUT_FOR_DELIVERY`) mặc dù đã giao thành công hoặc thất bại.
- **Khắc phục**: 
    - Đã bổ sung logic `shipment.setShipmentStatus(nextStatus)`.
    - Gọi `shipmentRepository.save(shipment)` để đồng bộ dữ liệu.
    - Cập nhật `deliveredAt` nếu trạng thái là `DELIVERED`.

### 1.2. Logic Phân vùng Phí vận chuyển (Hard-coded strings)
- **Vấn đề**: Sử dụng `contains("Bắc")` trực tiếp trong code gây rủi ro cao nếu dữ liệu DB không đồng nhất (thiếu dấu, viết tiếng Anh, v.v.).
- **Khắc phục**: 
    - Đã tạo Enum [Region.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/domain/enums/Region.java) để chuẩn hóa dữ liệu.
    - Sử dụng `keywords` để nhận diện thông minh các biến thể vùng miền.

## 2. Lỗi Cấu hình Spring Boot (Major)

### 2.1. Thiếu Annotation @Service & @Repository
- **Vấn đề**: Một số class quan trọng thiếu annotation khiến Spring không thể quản lý Bean, dẫn đến lỗi `NoSuchBeanDefinitionException`.
- **Khắc phục**:
    - Bổ sung `@Service` cho [AuthDomainService.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/domain/service/AuthDomainService.java).
    - Bổ sung `@Repository` cho [OrderRepository.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/persistence/repository/OrderRepository.java), [ShipmentRepository.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/persistence/repository/ShipmentRepository.java), và [ProductRepository.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/persistence/repository/ProductRepository.java).

## 3. Lỗi Kiểu dữ liệu (Minor)

### 3.1. Type Mismatch giữa DeliveryStatus và ShipmentStatus
- **Vấn đề**: Cố gắng gán trực tiếp `DeliveryStatus` cho biến kiểu `ShipmentStatus` gây lỗi biên dịch.
- **Khắc phục**: Đã tạo hàm chuyển đổi an toàn trong `DeliveryDomainService` hoặc xử lý thủ công qua `switch-case` để đảm bảo tính nhất quán giữa hai Enum khác nhau.

## 4. Đề xuất Tối ưu hóa (Best Practices)
- **Transactional**: Đảm bảo tất cả các Service có thao tác ghi vào nhiều bảng đều sử dụng `@Transactional` để tránh dữ liệu mồ côi (orphaned data).
- **Logging**: Đã bổ sung `@Slf4j` và các dòng log quan trọng để phục vụ việc debug khi có lỗi xảy ra trong môi trường production.

---
*Báo cáo được soạn thảo bởi Senior Developer nhằm đảm bảo hệ thống vận hành trơn tru và tuân thủ các chuẩn mực kiến trúc.*
