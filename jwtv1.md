# Phân tích Cơ chế Xác thực JWT (JSON Web Token) - Logistics System v1.0

Tài liệu này chi tiết hóa cách hệ thống Logistics của chúng ta xử lý bảo mật bằng JWT, được soạn thảo bởi Senior Developer.

## 1. Luồng hoạt động (Workflow)

Quy trình xác thực trong hệ thống diễn ra qua 5 bước chính:

1.  **Đăng nhập (Authentication)**: 
    - Người dùng gửi Email/Phone và Password đến API `/api/auth/login`.
    - [CustomUserDetailsService.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/security/CustomUserDetailsService.java) kiểm tra thông tin trong Database.
2.  **Khởi tạo Token**:
    - Nếu hợp lệ, [JwtTokenProvider.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/security/JwtTokenProvider.java) sẽ tạo một chuỗi JWT chứa các thông tin (Claims) như: `sub` (username), `iat` (thời điểm tạo), `exp` (thời điểm hết hạn).
    - Token được ký bằng thuật toán HMAC với `jwtSecret` lấy từ [application.properties](file:///d:/SE194498/system/src/main/resources/application.properties).
3.  **Lưu trữ tại Client**: 
    - Client (Frontend/Mobile) nhận Token và lưu vào LocalStorage hoặc Cookie.
4.  **Gửi Request kèm Token**:
    - Với mọi request tiếp theo (ví dụ: tạo vận đơn), Client gửi Token trong Header: `Authorization: Bearer <token>`.
5.  **Xác thực tại Server (Authorization)**:
    - [JwtAuthenticationFilter.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/security/JwtAuthenticationFilter.java) chặn request, trích xuất Token.
    - Token được giải mã và kiểm tra tính toàn vẹn. Nếu hợp lệ, thông tin người dùng được đưa vào `SecurityContextHolder` của Spring để các Service sử dụng.

## 2. Ưu điểm (Pros)

- **Stateless (Không lưu trạng thái)**: Server không cần lưu Session trong bộ nhớ hoặc DB. Điều này cực kỳ quan trọng cho khả năng mở rộng (Scalability) khi hệ thống có nhiều node chạy song song.
- **Hiệu năng cao**: Việc kiểm tra Token diễn ra rất nhanh bằng thuật toán toán học, không cần truy vấn Database cho mỗi request (trừ khi nạp thêm UserDetails).
- **Hỗ trợ Cross-Domain**: JWT hoạt động tốt trên nhiều tên miền khác nhau, phù hợp cho kiến trúc Microservices hoặc Mobile App tương tác với Web API.
- **Dữ liệu an toàn**: Nhờ chữ ký điện tử, Server có thể phát hiện ngay lập tức nếu Token bị chỉnh sửa trái phép.

## 3. Nhược điểm (Cons) & Hướng xử lý

- **Không thể thu hồi (Invalidation)**: Một khi Token đã phát hành, nó sẽ có hiệu lực cho đến khi hết hạn. Server khó có thể "logout" người dùng ngay lập tức.
    - *Giải pháp*: Đặt thời gian hết hạn ngắn (ví dụ: 1 giờ) và sử dụng Refresh Token.
- **Kích thước Token**: JWT chứa nhiều thông tin nên chuỗi Token khá dài, làm tăng băng thông cho mỗi request.
- **Bảo mật phía Client**: Nếu Client bị dính lỗi XSS, Token có thể bị đánh cắp.
    - *Giải pháp*: Khuyến khích lưu Token trong HttpOnly Cookie để chống XSS.

## 4. Cấu hình bảo mật trong Dự án

Toàn bộ quy trình trên được quản lý tập trung tại [SecurityConfig.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/security/SecurityConfig.java), nơi chúng ta phân quyền rõ ràng:
- `ADMIN`: Quản lý hệ thống.
- `STAFF`: Vận hành và giao hàng.
- `CUSTOMER`: Đặt hàng và theo dõi.

---
*Tài liệu được thiết kế nhằm giúp team hiểu rõ kiến trúc bảo mật của dự án.*
