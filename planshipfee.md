# Kế hoạch Triển khai Tính Phí Vận chuyển theo Phân vùng (Regional Shipping Fee)

Với vai trò là Senior Developer, tôi đề xuất hướng xử lý hệ thống tính phí vận chuyển dựa trên phân vùng địa lý (Region) để tối ưu hóa chi phí và đảm bảo tính linh hoạt khi mở rộng quy mô.

## 1. Phân tích Hiện trạng
- Đã có [ProvinceEntity.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/persistence/entity/ProvinceEntity.java) với trường `region` (Miền Bắc, Miền Trung, Miền Nam).
- Đã có [ShipmentEntity.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/persistence/entity/ShipmentEntity.java) lưu trữ thông tin tỉnh/thành nhận hàng và phí vận chuyển.
- Đã có [WarehouseEntity.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/persistence/entity/WarehouseEntity.java) xác định điểm xuất kho.

## 2. Chiến lược Phân vùng (Zoning Strategy)
Chúng ta sẽ chia phí vận chuyển thành 3 cấp độ dựa trên quan hệ giữa **Tỉnh Gửi (Kho)** và **Tỉnh Nhận**:

1.  **Nội vùng (Intra-Region):** Gửi và nhận trong cùng một miền (Ví dụ: Hà Nội -> Hải Phòng). Phí thấp nhất.
2.  **Liên vùng gần (Inter-Region Adjacent):** Gửi giữa các miền lân cận (Ví dụ: Miền Bắc -> Miền Trung). Phí trung bình.
3.  **Liên vùng xa (Inter-Region Remote):** Gửi giữa các miền cách xa nhau (Ví dụ: Miền Bắc -> Miền Nam). Phí cao nhất.

## 3. Công thức Tính toán (Calculation Formula)
`Tổng phí = Phí cơ bản + (Trọng lượng vượt mức * Đơn giá trọng lượng) + Phụ phí vùng miền`

Trong đó:
- **Phí cơ bản:** Áp dụng cho 0.5kg - 1kg đầu tiên.
- **Đơn giá trọng lượng:** Tính cho mỗi 0.5kg hoặc 1kg tiếp theo.
- **Phụ phí vùng miền:** Xác định bởi Matrix vùng miền.

## 4. Ma trận Phí Vùng miền (Regional Fee Matrix - Ví dụ)
| Từ \ Đến | Miền Bắc | Miền Trung | Miền Nam |
| :--- | :--- | :--- | :--- |
| **Miền Bắc** | 15.000đ | 30.000đ | 50.000đ |
| **Miền Trung** | 30.000đ | 15.000đ | 30.000đ |
| **Miền Nam** | 50.000đ | 30.000đ | 15.000đ |

## 5. Các bước triển khai kỹ thuật

### Bước 1: Cấu hình hằng số hoặc Database
Tạo [ShippingFeeDomainService.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/domain/service/ShippingFeeDomainService.java) để chứa logic nghiệp vụ. 
*Lưu ý: Trong thực tế, các con số này nên được lưu trong Database hoặc Configuration Server để có thể thay đổi mà không cần deploy lại code.*

### Bước 2: Xây dựng logic trong Domain Service
Hàm xử lý chính sẽ nhận vào:
- `sourceRegion`: Miền của kho xuất hàng.
- `destRegion`: Miền của khách hàng nhận hàng.
- `totalWeight`: Tổng trọng lượng đơn hàng.

### Bước 3: Tích hợp vào Quy trình đặt hàng
Khi tạo `Shipment`, gọi `ShippingFeeDomainService` để tính toán phí trước khi lưu vào [ShipmentEntity.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/infrastructure/persistence/entity/ShipmentEntity.java).

## 6. Ưu điểm của hướng tiếp cận này
- **Scalability:** Dễ dàng thêm các phân vùng đặc biệt (ví dụ: vùng sâu vùng xa, hải đảo) bằng cách thêm vào matrix.
- **Maintainability:** Logic tính phí tập trung tại một nơi (Domain Service), tuân thủ nguyên tắc Domain Driven Design.
- **Transparency:** Dễ dàng giải trình chi phí cho khách hàng dựa trên khoảng cách địa lý thực tế.

---
*Tài liệu được soạn thảo bởi Senior Developer.*
