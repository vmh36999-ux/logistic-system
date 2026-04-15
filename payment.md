# Quy trình Tích hợp MoMo Sandbox (Clean Architecture)

Tài liệu này chi tiết hóa cách triển khai tích hợp MoMo theo kiến trúc Clean Architecture để đảm bảo tính module, dễ bảo trì và mở rộng.

## 1. Cấu trúc các lớp (Layer Structure)

### 1.1. Domain Layer (Trái tim của nghiệp vụ)
- **[PaymentStatus.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/domain/enums/PaymentStatus.java)**: Định nghĩa các trạng thái thanh toán (PENDING, PAID, FAILED, REFUNDED).
- **[Payment.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/domain/model/Payment.java)**: Domain model lưu trữ thông tin thanh toán (Amount, TransactionId, OrderId).
- **[PaymentDomainService.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/domain/service/PaymentDomainService.java)**: Chứa logic nghiệp vụ thuần (ví dụ: kiểm tra xem đơn hàng có đủ điều kiện thanh toán không).

### 1.2. Application Layer (Điều phối Use Cases)
- **DTOs**: `PaymentRequest`, `PaymentResponse`, `MomoCallbackRequest`.
- **[PaymentApplicationService.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/application/service/PaymentApplicationService.java)**: 
    - Nhận yêu cầu từ Controller.
    - Gọi Infrastructure để tạo link thanh toán MoMo.
    - Cập nhật trạng thái đơn hàng sau khi có kết quả.

### 1.3. Infrastructure Layer (Giao tiếp bên ngoài)
- **MomoClient**: Thực hiện các cuộc gọi HTTP (RestTemplate/WebClient) đến Endpoint của MoMo.
- **[MomoSignatureUtils.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/security/MomoSignatureUtils.java)**: Chứa logic tạo chữ ký HMAC-SHA256 (không để ở Domain vì đây là chi tiết kỹ thuật của bên thứ 3).
- **Persistence**: Lưu trữ thông tin giao dịch vào Database qua `PaymentRepository`.

### 1.4. Interface Layer (API Endpoints)
- **[PaymentController.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/interfaces/controller/PaymentController.java)**: 
    - `POST /api/payment/momo/create`: Tạo yêu cầu và trả về `payUrl`.
    - `GET /api/payment/momo/callback`: Tiếp nhận khách hàng quay lại từ MoMo.
    - `POST /api/payment/momo/ipn`: Tiếp nhận thông báo ẩn (Server-to-Server) từ MoMo.

## 2. Luồng xử lý chi tiết (Workflow)

### Bước 1: Khởi tạo thanh toán
1.  **Controller** nhận `orderId`.
2.  **Application Service** lấy thông tin Order, kiểm tra số tiền.
3.  **Infrastructure (SignatureUtils)** tạo chữ ký bảo mật dựa trên `secretKey`.
4.  **Infrastructure (MomoClient)** gửi Request đến MoMo.
5.  **MoMo** trả về `payUrl`.
6.  **Hệ thống** trả `payUrl` cho Frontend để chuyển hướng người dùng.

### Bước 2: Xử lý kết quả (IPN/Callback)
1.  **MoMo** gửi thông báo về `notifyUrl` (IPN).
2.  **Controller** tiếp nhận các tham số (amount, resultCcde, signature...).
3.  **Infrastructure (SignatureUtils)** tính toán lại chữ ký từ các tham số nhận được và so sánh với `signature` MoMo gửi sang. **(Cực kỳ quan trọng để chống giả mạo)**.
4.  Nếu chữ ký khớp: **Application Service** cập nhật trạng thái đơn hàng thành `PAID` và ghi log tracking.
5.  Trả về kết quả cho MoMo để xác nhận đã nhận thông tin thành công.

## 3. Các lưu ý về Clean Architecture
- **Dependency Rule**: Lớp Application chỉ gọi Interface của Infrastructure, không phụ thuộc trực tiếp vào Implementation của MoMo. Điều này giúp bạn dễ dàng đổi sang VNPay hay ZaloPay sau này chỉ bằng cách thay đổi lớp Infrastructure.
- **Security**: Tuyệt đối không để `SecretKey` trong code, phải lấy từ `application.properties` thông qua `@Value` hoặc Environment Variables.

---
*Tài liệu được thiết kế bởi Senior Developer để đảm bảo tính hệ thống và bảo mật.*
