# Kế hoạch triển khai Logout với JWT và Redis Blacklist

Tài liệu này mô tả chi tiết phương án triển khai tính năng Logout an toàn cho ứng dụng Spring Boot 3 bằng cách sử dụng cơ chế Blacklist lưu trữ trong Redis.

## 1. Cập nhật JwtTokenProvider (JwtUtils)

Cần bổ sung phương thức để lấy thời gian hết hạn (Expiration Date) từ Token. Điều này giúp chúng ta tính toán được thời gian Token cần nằm trong Blacklist trước khi tự hủy.

**Vị trí**: [JwtTokenProvider.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/security/JwtTokenProvider.java)

**Logic bổ sung**:
- Sử dụng `Jwts.parser()` để giải mã claims.
- Trích xuất trường `expiration` từ Payload của JWT.

```java
public Date getExpirationDateFromToken(String token) {
    return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getExpiration();
}
```

## 2. Tạo BlacklistService

Service này chịu trách nhiệm tương tác trực tiếp với Redis để lưu trữ các Token đã bị đăng xuất.

**Vị trí**: `com.logistic.system.infrastructure.security.BlacklistService`

**Thành phần chính**:
- `StringRedisTemplate`: Để thao tác với dữ liệu String trong Redis.
- Phương thức `blacklistToken(String token, long durationMs)`: Lưu token vào Redis với key là chính token đó và giá trị bất kỳ (ví dụ: "logout").
- Phương thức `isTokenBlacklisted(String token)`: Kiểm tra xem token có tồn tại trong Redis hay không.

## 3. Cập nhật JwtAuthenticationFilter

Trước khi xác thực Token, Filter cần kiểm tra xem Token đó đã bị đưa vào Blacklist hay chưa.

**Vị trí**: [JwtAuthenticationFilter.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/security/JwtAuthenticationFilter.java)

**Logic thay đổi**:
- Inject `BlacklistService`.
- Trong hàm `doFilterInternal`, thêm điều kiện kiểm tra:
  ```java
  if (StringUtils.hasText(jwt) && !blacklistService.isTokenBlacklisted(jwt) && tokenProvider.validateToken(jwt)) {
      // Tiếp tục quy trình xác thực...
  }
  ```

## 4. Triển khai Endpoint /auth/logout

Xử lý yêu cầu đăng xuất từ người dùng.

**Vị trí**: [AuthController.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/interfaces/controller/AuthController.java)

**Quy trình thực hiện**:
1. Lấy Token từ Header `Authorization`.
2. **Xử lý chuỗi**: Loại bỏ tiền tố "Bearer " để lấy JWT nguyên bản.
3. Sử dụng `JwtTokenProvider` để lấy ngày hết hạn (`getExpirationDateFromToken`).
4. **Tính toán TTL (Time To Live)**:
   - `TTL = Expiration Date - Current Date`.
5. Gọi `BlacklistService` để lưu Token vào Redis với TTL vừa tính được.
6. Trả về thông báo đăng xuất thành công.

## 5. Các điểm bổ sung quan trọng (Missing Steps)

Sau khi scan qua quy trình, cần bổ sung các bước sau để hệ thống chạy ổn định:

### 5.1 Cấu hình SecurityConfig
Đảm bảo endpoint `/auth/logout` yêu cầu người dùng phải đăng nhập mới được thực hiện (authenticated).
```java
.requestMatchers("/auth/logout").authenticated()
```

### 5.2 Xử lý Exception
Trong `AuthController`, nếu Token gửi lên không hợp lệ hoặc đã hết hạn từ trước, hàm `getExpirationDateFromToken` có thể quăng lỗi. Cần bao bọc trong khối `try-catch` để trả về phản hồi phù hợp cho Client thay vì lỗi 500.

### 5.3 Đặt tiền tố (Prefix) cho Redis Key
Trong `BlacklistService`, nên lưu key kèm tiền tố để dễ quản lý trong Redis (ví dụ: `JWT_BLACKLIST:{token}`). Điều này giúp tránh xung đột với các dữ liệu cache khác.

### 5.4 Sử dụng StringRedisTemplate
Thay vì dùng `RedisTemplate<String, Object>`, việc dùng `StringRedisTemplate` sẽ tối ưu hơn cho việc lưu trữ Token (vì cả Key và Value đều là String), giúp tiết kiệm tài nguyên và dễ đọc dữ liệu trực tiếp từ Redis CLI.

## 6. Giải thích cách tính TTL và Cơ chế tự dọn dẹp

### Tại sao cần tính TTL?
Nếu chúng ta lưu Token vào Blacklist vĩnh viễn, bộ nhớ Redis sẽ bị đầy theo thời gian. Tuy nhiên, một JWT bản thân nó đã có thời gian hết hạn. Khi JWT hết hạn, nó sẽ không còn giá trị ngay cả khi không nằm trong Blacklist.

### Cách tính:
```java
long expirationTime = expirationDate.getTime(); // Thời điểm token hết hạn (ms)
long currentTime = System.currentTimeMillis();   // Thời điểm hiện tại (ms)
long ttl = expirationTime - currentTime;         // Thời gian còn lại của token
```

### Kết quả:
- Chúng ta chỉ yêu cầu Redis giữ Token trong Blacklist đúng bằng khoảng thời gian còn lại của nó.
- Sau khi hết thời gian này, Redis sẽ **tự động xóa** key đó (giúp dọn dẹp bộ nhớ).
- Nếu người dùng cầm Token cũ đã hết hạn quay lại, nó sẽ bị chặn bởi hàm `validateToken()` thông thường của JWT chứ không cần đến Blacklist nữa.

---
*Lưu ý: Đảm bảo Redis đã được cài đặt và cấu hình sẵn sàng trong dự án.*
