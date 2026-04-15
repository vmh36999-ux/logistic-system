# 🚀 Hướng dẫn chia sẻ và khởi chạy dự án cho đồng nghiệp

Để đồng nghiệp có thể chạy được dự án này trên máy của họ sau khi bạn đã đóng gói, hãy thực hiện theo các bước sau:

## 1. Đối với bạn (Người gửi)
Bạn cần gửi toàn bộ thư mục dự án (bao gồm `Dockerfile`, `docker-compose.yml`, và mã nguồn) cho đồng nghiệp qua Git (GitHub/GitLab) hoặc nén thành file `.zip`.

## 2. Đối với đồng nghiệp (Người nhận)

### Bước 1: Cài đặt công cụ
Đảm bảo máy đã cài đặt:
- **Docker Desktop** (Đã bao gồm Docker Compose).
- **Git** (Nếu nhận qua Git).

### Bước 2: Khởi chạy ứng dụng
Mở Terminal (PowerShell hoặc CMD) tại thư mục gốc của dự án và chạy lệnh:

```bash
# Lệnh này sẽ tự động build image và chạy DB + App
docker-compose up -d --build
```

### Bước 3: Kiểm tra
- **Ứng dụng**: Truy cập `http://localhost:8080`
- **Tài liệu API (Swagger)**: `http://localhost:8080/swagger-ui/index.html`
- **Database**: Có thể kết nối qua Tool (DBeaver/Navicat) tại `localhost:3306` với user `root` / pass `thuctd2k5@ZZ`.

---

## 💡 Các mẹo nhỏ khi chia sẻ:

1. **Nếu đồng nghiệp chỉ muốn dùng DB của bạn để code local:**
   Họ chỉ cần chạy:
   ```bash
   docker-compose -f docker-compose-db.yml up -d
   ```

2. **Nếu bạn muốn gửi Image đã build sẵn (Không cần build lại):**
   Bạn có thể đẩy (push) image lên **Docker Hub**:
   ```bash
   docker tag logistics-system:v1 your-docker-hub-username/logistics-system:v1
   docker push your-docker-hub-username/logistics-system:v1
   ```
   Sau đó đồng nghiệp chỉ cần sửa `image` trong `docker-compose.yml` thành tên image trên Docker Hub của bạn.

3. **Lưu ý về Port:**
   Hãy đảm bảo máy đồng nghiệp không có ứng dụng nào đang chiếm cổng `8080`, `3306`, hoặc `6379`.
