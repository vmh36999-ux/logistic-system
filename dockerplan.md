# Hướng dẫn Đóng gói Ứng dụng với Docker (Docker Plan)

Tài liệu này hướng dẫn chi tiết cách viết Dockerfile và quy trình đóng gói ứng dụng Spring Boot Logistics System thành Docker Image.

## 1. Viết Dockerfile

Để tối ưu hóa kích thước và bảo mật, chúng ta sẽ sử dụng phương pháp **Multi-stage build**.

Tạo một file tên là `Dockerfile` (không có đuôi mở rộng) tại thư mục gốc của dự án với nội dung sau:

```dockerfile
# Giai đoạn 1: Build ứng dụng
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy file cấu hình maven trước để tận dụng cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy mã nguồn và đóng gói
COPY src ./src
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy ứng dụng
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy file jar từ giai đoạn build
COPY --from=build /app/target/*.jar app.jar

# Khai báo cổng ứng dụng
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 2. Quy trình đóng gói (Packaging Process)

Thực hiện các bước sau để đóng gói ứng dụng:

### Bước 1: Build Docker Image
Mở terminal tại thư mục gốc và chạy lệnh:
```bash
docker build -t logistics-system:v1 .
```

### Bước 2: Kiểm tra Image đã tạo
```bash
docker images
```

### Bước 3: Chạy Container (Thử nghiệm)
```bash
docker run -p 8080:8080 logistics-system:v1
```

## 3. Triển khai với Docker Compose (Khuyên dùng)

Vì ứng dụng cần kết nối với **MySQL** và **Redis**, việc sử dụng `docker-compose.yml` sẽ giúp quản lý các dịch vụ dễ dàng hơn.

Tạo file `docker-compose.yml` tại thư mục gốc:

```yaml
version: '3.8'
services:
  # Ứng dụng Spring Boot
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/logistics_db?createDatabaseIfNotExist=true
      - SPRING_REDIS_HOST=redis-cache
    depends_on:
      - mysql-db
      - redis-cache

  # Database MySQL
  mysql-db:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=thuctd2k5@ZZ
      - MYSQL_DATABASE=logistics_db
    ports:
      - "3306:3306"

  # Cache Redis
  redis-cache:
    image: redis:alpine
    ports:
      - "6379:6379"
```

### Cách khởi chạy toàn bộ hệ thống:
Chạy lệnh duy nhất:
```bash
docker-compose up -d
```

## 4. Lưu ý quan trọng
- **Java Version**: Dự án sử dụng Java 21, vì vậy Base Image phải là `eclipse-temurin:21`.
- **Context Path**: Khi chạy trong Docker, URL Swagger UI vẫn tuân theo context path: `http://localhost:8080/api/v3/swagger-ui/index.html`.
- **Biến môi trường**: Các thông tin nhạy cảm (như mật khẩu Mail, MoMo) nên được truyền qua `environment` trong docker-compose để bảo mật hơn.
