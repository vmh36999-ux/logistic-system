# 🛠️ Plan Fix Bug & Security Hardening (Senior Audit Findings)

Dựa trên kết quả Audit dự án Logistics System, dưới đây là lộ trình chi tiết để khắc phục các lỗi nghiêm trọng (Critical) và tối ưu hóa hệ thống lên mức Production-Ready.

---

## 🛑 Phase 1: Critical Security Fixes (Cấp bách)

Mục tiêu: Đóng các lỗ hổng bảo mật có thể gây thất thoát tiền bạc và dữ liệu.

### 1.1 Xác thực chữ ký MoMo Callback (Signature Verification)
*   **Vấn đề:** Hiện tại `PaymentController` chấp nhận mọi POST request vào `/momo-callback` mà không kiểm tra chữ ký.
*   **Giải pháp:**
    *   Sử dụng `MomoSignatureUtils` để tính toán lại chữ ký từ các tham số MoMo gửi về.
    *   So sánh với tham số `signature` trong payload. Chỉ xử lý logic nếu khớp.
*   **File ảnh hưởng:** [PaymentApplicationService.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/application/service/PaymentApplicationService.java), [MomoServiceImpl.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/service/impl/MomoServiceImpl.java).

### 1.2 Thắt chặt Security Config
*   **Vấn đề:** `/api/payments/**` đang để `.permitAll()`, cho phép truy cập trái phép.
*   **Giải pháp:**
    *   Chỉ permit công khai cho `/api/payments/momo-callback` và `/api/payments/momo-ipn`.
    *   Endpoint `/api/payments/momo/create` phải yêu cầu quyền `CUSTOMER`.
*   **File ảnh hưởng:** [SecurityConfig.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/security/SecurityConfig.java).

---

## 🛠️ Phase 2: Logic & Architecture Fixes (Độ ổn định)

Mục tiêu: Sửa các lỗi logic gây crash app và chuẩn hóa cấu trúc.

### 2.1 Sửa lỗi NonUniqueResultException trong Repository
*   **Vấn đề:** `findByCustomer_CustomerId` trả về `Optional` nhưng thực tế khách có thể có nhiều đơn hàng.
*   **Giải pháp:** Đổi kiểu trả về thành `List<OrderEntity>` hoặc sửa logic tìm kiếm chính xác (theo `orderId` hoặc `orderCode`).
*   **File ảnh hưởng:** [OrderRepository.java](file:///d:/SE194498/system/src/main/infrastructure/persistence/repository/OrderRepository.java).

### 2.2 Triển khai Global Exception Handler
*   **Vấn đề:** Trả về lỗi 500 kèm stack trace khi có RuntimeException.
*   **Giải pháp:**
    *   Tạo `@RestControllerAdvice`.
    *   Định nghĩa `ErrorResponse` chuẩn (status, message, timestamp).
    *   Bắt các lỗi `EntityNotFoundException`, `AccessDeniedException`, v.v.
*   **Thư mục mới:** `com.logistic.system.infrastructure.exception`.

### 2.3 Chuẩn hóa DTO Packages
*   **Vấn đề:** Tồn tại 2 package `reponse` (sai chính tả) và `response`.
*   **Giải pháp:** Refactor toàn bộ code sang package `response`, xóa bỏ package `reponse`.

---

## 🚀 Phase 3: Performance & Clean Code (Tối ưu hóa)

Mục tiêu: Tăng tốc độ phản hồi và làm sạch mã nguồn.

### 3.1 Khắc phục N+1 Query trong Place Order
*   **Vấn đề:** Gọi DB tìm sản phẩm trong vòng lặp.
*   **Giải pháp:** 
    *   Lấy toàn bộ List Product ID từ request.
    *   Sử dụng `productRepository.findAllById(ids)` một lần duy nhất trước vòng lặp.
*   **File ảnh hưởng:** [OrderApplicationService.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/application/service/OrderApplicationService.java).

### 3.2 Tăng cường ID Generation (Order Code)
*   **Vấn đề:** UUID substring(0,8) dễ trùng lặp.
*   **Giải pháp:** Sử dụng kết hợp `Date + NanoTime + Random` hoặc `Snowflake ID`.
*   **File ảnh hưởng:** [OrderDomainService.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/domain/service/OrderDomainService.java).

---

## 📋 Check-list Kiểm tra sau khi Fix
- [ ] MoMo Callback trả về 401/403 nếu gửi sai chữ ký.
- [ ] Không thể tạo thanh toán nếu không gửi kèm JWT Token hợp lệ.
- [ ] Log đơn hàng hiển thị chính xác phí vận chuyển sau khi tạo Shipment.
- [ ] API trả về JSON lỗi đẹp mắt thay vì trang trắng 500.

---
**Người lập plan:** Senior Backend Engineer (AI Assistant)
**Ngày:** 2026-04-15
