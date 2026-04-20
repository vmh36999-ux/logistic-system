# Logistic System

> Hệ thống Backend hiệu năng cao cho vận hành Logistics, được xây dựng dựa trên nguyên lý Clean Architecture.

***

## 🚀 Điểm nổi bật (Highlights)

- **Triển khai Clean Architecture**: Phân tách trách nhiệm nghiêm ngặt giữa các tầng Domain, Application, Infrastructure và Interfaces.
- **Quản lý Tồn kho Nguyên tử (Atomic)**: Đảm bảo tính nhất quán dữ liệu bằng `@Transactional` để xử lý các luồng nghiệp vụ kho bãi phức tạp.
- **Bảo mật Nâng cao**: Xác thực JWT Stateless kết hợp với cơ chế Blacklist dựa trên Redis hiệu năng cao để xử lý đăng xuất an toàn.
- **Mapping An toàn Kiểu dữ liệu**: Tự động hóa việc chuyển đổi DTO-to-Entity bằng MapStruct nhằm giữ cho Domain Model luôn sạch.

***

## 🛠 Tech Stack

- **Java 21**: Tận dụng các tính năng hiện đại như Records và Pattern Matching để mã nguồn sạch hơn.
- **Spring Boot 3.4**: Framework cốt lõi cho DI, AOP và phát triển REST API nhanh chóng.
- **MySQL**: Cơ sở dữ liệu quan hệ cho dữ liệu logistics và đơn hàng có cấu trúc.
- **Redis**: Lưu trữ độ trễ thấp cho quản lý phiên (session) và danh sách đen (blacklist) bảo mật.
- **MapStruct**: Mapping hiệu năng cao để tách biệt các tầng và ngăn chặn rò rỉ domain.
- **Spring Security & JWT**: Tiêu chuẩn công nghiệp cho xác thực và phân quyền.

***

## 🏗 Kiến trúc (Architecture)

Dự án tuân thủ **Clean Architecture** để đảm bảo logic nghiệp vụ cốt lõi không bị phụ thuộc vào các framework bên ngoài:

- **`domain`**: Chứa các quy tắc nghiệp vụ thuần túy (Entities, Value Objects, Domain Services). Không phụ thuộc vào thư viện bên ngoài.
- **`application`**: Điều phối các ca sử dụng (Use Cases). Quản lý DTO và phối hợp các domain services.
- **`infrastructure`**: Triển khai các chi tiết kỹ thuật (Repositories, Security, Tích hợp API bên ngoài).
- **`interfaces`**: Các điểm vào của hệ thống (REST Controllers).

***

## 🧠 Quyết định Kỹ thuật then chốt (Key Engineering Decisions)

### 1. Khấu trừ Tồn kho theo Trạng thái (State-Driven)

Khác với các triển khai thông thường trừ kho ngay khi tạo đơn, hệ thống này tuân theo quy trình thực tế:

- **`PENDING`**: Chỉ kiểm tra tính sẵn có mà không khóa tài nguyên (tránh vấn đề tồn kho ảo).
- **`CONFIRMED`**: Thực hiện trừ kho nguyên tử thông qua `InventoryDomainService`.
- **`CANCELLED`**: Tự động hoàn kho chỉ khi đơn hàng đã được xác nhận trước đó.

### 2. Phân tách Tầng nghiêm ngặt (Data Flow)

Để tránh tình trạng "Fat Entities" và rò rỉ các vấn đề DB:
`Request DTO` ➔ `Domain Model` ➔ `JPA Entity` ➔ `Database`

- MapStruct xử lý chuyển đổi tại thời điểm biên dịch (compile-time) để không gây ảnh hưởng đến hiệu năng khi chạy (zero runtime overhead).
- Domain models luôn "sạch" và dễ dàng viết unit test mà không cần các annotation của JPA.

### 3. Tính Toàn vẹn của Giao dịch (Transaction Integrity)

Các thao tác quan trọng (như `placeOrder` hoặc `cancelOrder`) được bao bọc trong `@Transactional`. Điều này đảm bảo rằng nếu cập nhật thanh toán thất bại sau khi đổi kho, toàn bộ thao tác sẽ được rollback, giữ cho hệ thống luôn ở trạng thái nhất quán.

***

## 🔒 Triển khai Bảo mật

- **Xác thực**: Phiên làm việc stateless dựa trên JWT.
- **Thu hồi Token (Revocation)**: Vì JWT không thể bị vô hiệu hóa một cách tự nhiên, tôi đã triển khai **Redis Blacklist**. Khi đăng xuất, token sẽ được lưu vào Redis với thời gian sống (TTL) khớp với thời gian hết hạn của nó, ngăn chặn việc tái sử dụng.
- **Phân quyền dựa trên vai trò (RBAC)**: Kiểm soát chi tiết quyền hạn cho `ADMIN`, `STAFF` và `CUSTOMER`.

***

## 📖 Ví dụ API

### Xác thực (`POST /api/auth/login`)

**Request:**

```json
{
  "username": "admin@logistic.com",
  "password": "securepassword"
}
```
đính kèm ảnh: <img width="380" height="162" alt="Screenshot 2026-04-20 231445" src="https://github.com/user-attachments/assets/8fbf5b53-4dda-454b-86a7-b5c10275b7e4" />
**Response:**

```json
{
  "accessToken": "eyJhbGci...",
  "tokenType": "Bearer",
  "username": "admin@logistic.com",
  "role": "ADMIN"
}
```
<img width="861" height="142" alt="image" src="https://github.com/user-attachments/assets/c0421c02-5a15-4190-aac4-163246262448" />
***

## 💻 Cài đặt và Khởi chạy

### Yêu cầu hệ thống

- JDK 21+
- MySQL 8.0+
- Redis Server

### Các bước cài đặt

1. Clone repository.
2. Cập nhật `src/main/resources/application.yml` với thông tin DB và Redis của bạn.
3. Build dự án:
   ```bash
   mvn clean install
   ```
4. Chạy ứng dụng:
   ```bash
   mvn spring-boot:run
   ```

*Truy cập tài liệu Swagger tại:* `http://localhost:8080/swagger-ui/index.html`

***

## 📈 Định hướng phát triển

- [ ] Triển khai **Kiến trúc hướng sự kiện (Event-Driven Architecture)** với Kafka để theo dõi trạng thái vận chuyển theo thời gian thực.
- [ ] Thêm **Docker Compose** để khởi tạo nhanh toàn bộ hệ thống (MySQL, Redis, Backend).
- [ ] Triển khai **Rate Limiting** để bảo vệ các API public khỏi spam request.
- [ ] Xây dựng **Frontend đơn giản (React/Next.js)** để hiển thị luồng đơn hàng và vận chuyển (do hiện tại hệ thống chưa có giao diện).

***

