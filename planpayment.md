# Kế hoạch Triển khai Thanh toán Online (MoMo Sandbox)

Với vai trò là Senior Developer, tôi đề xuất kế hoạch tích hợp cổng thanh toán MoMo Sandbox cho hệ thống Logistics. MoMo sử dụng cơ chế chữ ký bảo mật rất chặt chẽ, đảm bảo an toàn cho các giao dịch tài chính.

## 1. Kiến trúc Tổng quan

Sử dụng mô hình **MoMo All-in-One (AIOC)** - Redirect:

1. **Hệ thống**: Tạo yêu cầu thanh toán (Payment Request) -> Nhận `payUrl` từ MoMo -> Redirect khách hàng.
2. **Khách hàng**: Thực hiện quét mã QR hoặc đăng nhập MoMo để thanh toán.
3. **MoMo**: Trả kết quả về qua **IPN (Notify URL)** cho Server và **Redirect (Return URL)** cho Client.

## 2. Các bước triển khai kỹ thuật

### Bước 1: Lấy thông tin MoMo Sandbox

Đăng ký và lấy thông tin tại [MoMo Developer](https://developers.momo.vn/):

- `Partner Code`: Mã đối tác.
- `Access Key`: Khóa truy cập.
- `Secret Key`: Khóa bí mật (Dùng để tạo chữ ký HMAC SHA256).
- `Endpoint`: `https://test-payment.momo.vn/v2/gateway/api/create`

### Bước 2: Cấu hình hệ thống

Thêm vào [application.properties](file:///d:/SE194498/system/src/main/resources/application.properties):

```properties
payment.momo.partner-code=YOUR_PARTNER_CODE
payment.momo.access-key=YOUR_ACCESS_KEY
payment.momo.secret-key=YOUR_SECRET_KEY
payment.momo.endpoint=https://test-payment.momo.vn/v2/gateway/api/create
payment.momo.return-url=http://localhost:8080/api/payment/momo-callback
payment.momo.notify-url=http://your-public-domain.com/api/payment/momo-ipn
```

### Bước 3: Xây dựng Domain & Infrastructure

- **[PaymentStatus.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/domain/enums/PaymentStatus.java)**: Enum (PENDING, SUCCESS, FAILED).
- **[PaymentApplicationService.java](file:///d:/SE194498/system/src/main/java/com/logistic/system/application/service/PaymentApplicationService.java)**:
  - Hàm `createMomoPayment(Order order)`: Tạo chữ ký `signature` bằng thuật toán **HMAC SHA256** với chuỗi raw data theo định dạng của MoMo.
  - Hàm `verifySignature(Map params)`: Xác thực tính toàn vẹn của dữ liệu MoMo gửi về.

### Bước 4: Tích hợp vào luồng Order

- Khi khách hàng chọn MoMo, hệ thống gọi MoMo API để lấy `payUrl`.
- Lưu `orderId` và `requestId` để đối soát trạng thái sau này.

## 3. Quy trình bảo mật (Security)

MoMo yêu cầu tạo chuỗi raw data theo thứ tự bảng chữ cái các key trước khi hash:
`accessKey=$accessKey&amount=$amount&extraData=$extraData&ipnUrl=$ipnUrl&orderId=$orderId&orderInfo=$orderInfo&partnerCode=$partnerCode&redirectUrl=$redirectUrl&requestId=$requestId&requestType=$requestType`

**Lưu ý**: Bất kỳ sự sai lệch nào về thứ tự hoặc dữ liệu đều dẫn đến lỗi `Signature mismatch`.

## 4. Kế hoạch Kiểm thử (Testing)

1. **Sử dụng App MoMo Sandbox**: Cài đặt ứng dụng MoMo bản dành cho Developer để quét mã QR test.
2. **Test Case 1**: Thanh toán thành công với số tiền nhỏ.
3. **Test Case 2**: Kiểm tra tính đúng đắn của `extraData` (dùng để truyền thông tin bổ sung như mã vận đơn).
4. **Test Case 3**: Xử lý trường hợp người dùng hủy thanh toán giữa chừng.

***

*Tài liệu được cập nhật bởi Senior Developer nhằm tối ưu hóa trải nghiệm thanh toán qua MoMo.*
