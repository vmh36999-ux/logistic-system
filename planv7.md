# Kế hoạch triển khai Redis vào hệ thống Logistics (Plan v7)

## 1. Redis là gì?
Redis (Remote Dictionary Server) là một hệ thống lưu trữ dữ liệu trong bộ nhớ (in-memory data structure store), được sử dụng như một database, cache và message broker. 

### Đặc điểm nổi bật:
- **Tốc độ cực nhanh:** Vì dữ liệu được lưu trữ trong RAM, Redis có thể thực hiện hàng trăm nghìn thao tác đọc/ghi mỗi giây.
- **Hỗ trợ nhiều kiểu dữ liệu:** String, List, Set, Hash, Sorted Set...
- **Persistence:** Có khả năng lưu dữ liệu xuống đĩa cứng (RDB/AOF) để tránh mất dữ liệu khi restart.

## 2. Tại sao cần Redis trong dự án này?
Dự án Logistics của chúng ta có nhiều dữ liệu mang tính "tĩnh" hoặc ít thay đổi nhưng lại được truy vấn rất nhiều lần:
- **Danh mục hành chính:** Tỉnh/Thành phố, Quận/Huyện, Phường/Xã.
- **Thông tin sản phẩm:** Danh sách sản phẩm, giá cả.
- **Cấu hình hệ thống:** Các phí ship, quy định vận chuyển.

Sử dụng Redis giúp:
- Giảm tải cho database chính (MySQL).
- Tăng tốc độ phản hồi API cho người dùng.
- Cải thiện hiệu năng tổng thể của hệ thống.

## 3. Chi tiết triển khai hiện tại

### 3.1 Cấu hình Maven (pom.xml)
Chúng ta đã thêm dependency để tích hợp Spring Boot với Redis:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 3.2 Cấu hình ứng dụng (application.properties)
Kết nối đến server Redis (mặc định chạy tại localhost:6379):
```properties
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

### 3.3 Java Configuration
File [RedisConfig.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/security/RedisConfig.java) đã được thiết lập để quản lý `RedisTemplate` với serializer JSON giúp dữ liệu dễ đọc hơn.

Bên cạnh đó, `@EnableCaching` đã được bật tại [SystemApplication.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/SystemApplication.java).

## 4. Phương hướng triển khai đề xuất (Architecture Options)

Để khắc phục các lỗi hiện tại và đảm bảo hệ thống dễ bảo trì, chúng ta có 2 phương án chính:

### Phương án A: Sử dụng Spring Cache (@Cacheable) - NHANH & ĐƠN GIẢN
Đây là cách tiếp cận chuẩn của Spring Boot. Bạn chỉ cần đặt Annotation lên phương thức cần cache.

*   **Ưu điểm:** Cực kỳ nhanh, không cần viết code triển khai logic cache.
*   **Cách làm:**
    1.  Xóa file `ProvinceRepositoryImpl.java` (vì Spring Data JPA tự sinh implementation).
    2.  Thêm `@Cacheable` vào [ProvinceRepository.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/persistence/repository/ProvinceRepository.java).
    3.  Thực hiện mapping Entity -> Domain ở tầng **Service**.

### Phương án B: Sử dụng Custom Repository (Nếu cần kiểm soát sâu) - LINH HOẠT & CHUẨN DDD
Nếu bạn muốn dùng `RedisTemplate` để tùy chỉnh TTL hoặc thực hiện mapping ngay tại tầng Infrastructure để trả về **Domain Model**, bạn phải dùng mô hình **Custom Repository**.

*   **Kiến trúc:**
    1.  `ProvinceRepository` (Interface JPA): Chỉ quản lý DB.
    2.  `ProvinceDomainRepository` (Interface Domain): Định nghĩa các phương thức trả về Domain Model.
    3.  `ProvinceDomainRepositoryImpl` (Implementation): Inject cả `ProvinceRepository` (JPA) và `RedisTemplate` để xử lý cache + mapping.

## 5. Tại sao ProvinceRepositoryImpl hiện tại gặp lỗi?
File [ProvinceRepositoryImpl.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/persistence/redis/ProvinceRepositoryImpl.java) bạn đang viết gặp các vấn đề sau:
1.  **Sai kiểu trả về:** Interface `ProvinceRepository` yêu cầu trả về `ProvinceEntity`, nhưng bạn lại trả về `Province` (Domain).
2.  **Thiếu phương thức:** Khi `implements ProvinceRepository`, bạn bắt buộc phải viết code cho hàng chục phương thức của `JpaRepository` (save, delete, findById...).
3.  **Lỗi logic Spring Data:** Spring Data JPA sẽ cố gắng tự tạo bean cho interface, việc bạn tạo class trùng tên `Impl` mà không tuân thủ naming convention của Custom Repository sẽ gây xung đột Bean.

## 6. Lộ trình thực hiện tiếp theo (Next steps)
1.  **Quyết định phương án:** Nên chọn **Phương án A** cho các danh mục tĩnh (Tỉnh/Huyện/Xã) để code sạch và nhanh nhất.
2.  **Cấu hình TTL:** Thiết lập thời gian sống cho từng loại cache trong `application.properties`.
3.  **Xử lý Eviction:** Thêm `@CacheEvict` vào các hàm thêm/sửa/xóa để đảm bảo cache luôn mới.
