# Clean Architecture - Logistics System

## 1. Tổng quan Kiến trúc

Dự án tuân thủ mô hình **Clean Architecture (DDD-based)** với 4 tầng chính:

```
interfaces/          →  Giao diện (Controllers, DTOs)
        ↓
application/         →  Tầng Ứng dụng (Services, Use Cases)
        ↓
domain/              →  Tầang Domain (Models, Enums, Business Rules)
        ↓
infrastructure/      →  Tầng Hạ tầng (Persistence, Security, External Services)
```

---

## 2. Chi tiết từng tầng (Layer Details)

### 2.1 Domain Layer (`com.logistic.system.domain`)
**Nhiệm vụ**: Chứa các quy tắc nghiệp vụ cốt lõi, không phụ thuộc vào framework nào.

| Thư mục | Mô tả |
|---------|--------|
| `model/` | Domain Model (POJO) - Đại diện cho các thực thể nghiệp vụ: Account, Customer, Order, Shipment... |
| `enums/` | Các kiểu liệt kê: AccountRole, OrderStatus, PaymentStatus, Gender... |
| `service/` | Domain Service - Chứa logic nghiệp vụ phức tạp không thuộc về Model: OrderDomainService, PaymentDomainService... |

**Domain Models**: Account, Customer, DeliveryAttempt, District, Inventory, Order, OrderItem, Payment, Product, Province, Shipment, ShipmentItem, ShipmentTrackingLog, ShippingFee, Staff, Ward, Warehouse.

**Domain Services**:
- AuthDomainService, CustomerDomainService, DeliveryDomainService
- InventoryDomainService, OrderDomainService, OrderItemDomainService
- PaymentDomainService, ProductDomainService, ShipmentDomainService
- ShipmentItemDomainService, ShipmentTrackingDomainService, ShippingFeeDomainService

---

### 2.2 Application Layer (`com.logistic.system.application`)
**Nhiệm vụ**: Điều phối luồng nghiệp vụ, giao tiếp giữa Domain và Infrastructure.

| Thư mục | Mô tả |
|---------|--------|
| `service/` | Application Service - Chứa các Use Case: AuthApplicationService, OrderApplicationService, ProductApplicationService... |
| `dto/request/` | DTO nhận yêu cầu từ Client: LoginRequest, RegisterRequest, OrderRequest, ProductRequest... |
| `dto/reponse/` | DTO trả về cho Client: AuthResponse, OrderResponse, ProductResponse... |

**Application Services**:
- AuthApplicationService, CustomerApplicationService, DeliveryAttemptApplicationService
- EmailApplicationService, InventoryApplicationService, OrderApplicationService
- OrderItemApplicationService, PaymentApplicationService, ProductApplicationService
- ShipmentApplicationService, ShipmentItemApplicationService, ShipmentTrackingApplicationService

**Request DTOs**: CustomerRequest, DeliveryAttemptRequest, InventoryRequest, LoginRequest, MomoCallbackRequest, OrderItemRequest, OrderRequest, PaymentRequest, ProductRequest, RegisterRequest, ShipmentItemRequest, ShipmentRequest, TrackingLogRequest.

**Response DTOs**: AuthResponse, CustomerResponse, InventoryResponse, OrderItemResponse, OrderResponse, PaymentResponse, ProductResponse, ShipmentItemResponse, ShipmentResponse.

---

### 2.3 Infrastructure Layer (`com.logistic.system.infrastructure`)
**Nhiệm vụ**: Cài đặt các chi tiết kỹ thuật: database, security, external APIs.

| Thư mục | Mô tả |
|---------|--------|
| `persistence/entity/` | JPA Entity - Ánh xạ trực tiếp xuống database: AccountEntity, OrderEntity, ProductEntity... |
| `persistence/repository/` | Spring Data JPA Repository - Thao tác với database: AccountRepository, OrderRepository... |
| `mapper/` | MapStruct Mapper - Chuyển đổi Entity ↔ Domain Model ↔ DTO |
| `security/` | Cấu hình Spring Security, JWT, Redis Cache, Password Encoder |
| `service/impl/` | Triển khai các dịch vụ bên ngoài: MomoServiceImpl (thanh toán MoMo) |

**JPA Entities** (16 bảng): AccountEntity, CustomerEntity, DeliveryAttemptEntity, DistrictEntity, InventoryEntity, OrderEntity, OrderItemEntity, PaymentEntity, ProductEntity, ProvinceEntity, ShipmentEntity, ShipmentItemEntity, ShipmentTrackingLogEntity, ShippingFeeEntity, StaffEntity, WardEntity, WarehouseEntity.

**Repositories**: AccountRepository, CustomerRepository, DeliveryAttemptRepository, DistrictRepository, InventoryRepository, OrderItemRepository, OrderRepository, PaymentRepository, ProductRepository, ProvinceRepository, ShipmentItemRepository, ShipmentRepository, ShipmentTrackingLogRepository, WardRepository, WarehouseRepository.

**Mappers**: AccountMapper, CustomerMapper, InventoryMapper, LocationMapper, OrderMapper, PaymentMapper, ProductMapper, ProductOrderMapper, ShipmentMapper, UserWarehouseMapper.

**Security Classes**: BlacklistService, CustomUserDetailsService, JwtAuthenticationFilter, JwtTokenProvider, MomoSignatureUtils, OpenApiConfig, RedisConfig, RestTemplateConfig, SecurityConfig.

---

### 2.4 Interfaces Layer (`com.logistic.system.interfaces`)
**Nhiệm vụ**: Giao diện API REST, tiếp nhận HTTP request và trả response.

| Thư mục | Mô tả |
|---------|--------|
| `controller/` | REST Controllers - Nhận request từ Client: AuthController, OrderController, ProductController... |
| `dto/` | Chứa DTO dùng chung cho interfaces (request/response cụ thể hơn) |

**Controllers**: AuthController, CustomerController, DeliveryAttemptController, InventoryController, OrderController, PaymentController, ProductController, ShipmentController, ShipmentItemController, ShipmentTrackingController.

---

## 3. Sơ đồ quan hệ giữa các tầng

```
Client (HTTP Request)
       ↓
┌─────────────────────────────────┐
│    Interfaces Layer              │
│  (Controllers, DTOs)            │
└─────────────────────────────────┘
              ↓
┌─────────────────────────────────┐
│    Application Layer            │
│  (Services - Use Cases)         │
└─────────────────────────────────┘
              ↓
┌─────────────────────────────────┐
│    Domain Layer                 │
│  (Models, Enums, Domain Svc)    │
└─────────────────────────────────┘
              ↓
┌─────────────────────────────────┐
│    Infrastructure Layer         │
│  (JPA, Security, Redis, MoMo)   │
└─────────────────────────────────┘
              ↓
      MySQL Database
```

---

## 4. Cấu trúc Package theo Domain Module

```
com.logistic.system
├── application/
│   └── service/
│       ├── AuthApplicationService.java      # Đăng nhập/đăng ký
│       ├── OrderApplicationService.java      # Quản lý đơn hàng
│       ├── ProductApplicationService.java    # Quản lý sản phẩm
│       ├── CustomerApplicationService.java   # Quản lý khách hàng
│       ├── PaymentApplicationService.java   # Xử lý thanh toán MoMo
│       ├── ShipmentApplicationService.java # Quản lý vận đơn
│       └── ...
│
├── domain/
│   ├── model/                              # Domain Entities
│   │   ├── Account.java
│   │   ├── Order.java
│   │   └── ...
│   ├── enums/                              # Trạng thái, loại
│   │   ├── AccountRole.java (ADMIN, STAFF, CUSTOMER)
│   │   ├── OrderStatus.java (PENDING, CONFIRMED, SHIPPING, DELIVERED)
│   │   └── ...
│   └── service/                            # Business Logic
│       └── OrderDomainService.java
│
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/                         # JPA Entities (Database Tables)
│   │   │   ├── AccountEntity.java
│   │   │   └── ...
│   │   └── repository/                      # Data Access
│   │       ├── AccountRepository.java
│   │       └── ...
│   ├── mapper/                             # MapStruct (Entity ↔ Model ↔ DTO)
│   ├── security/                           # Spring Security, JWT
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── BlacklistService.java
│   │   └── SecurityConfig.java
│   └── service/impl/                       # External Services
│       └── MomoServiceImpl.java
│
└── interfaces/
    └── controller/                         # REST API Endpoints
        ├── AuthController.java
        ├── OrderController.java
        └── ...
```

---

## 5. Các Module nghiệp vụ chính (Business Modules)

| Module | Mô tả | Entity chính |
|--------|--------|--------------|
| **Auth** | Đăng nhập, đăng ký, đăng xuất (JWT + Redis Blacklist) | Account |
| **Customer** | Quản lý thông tin khách hàng, địa chỉ | Customer, Province, District, Ward |
| **Product** | Quản lý sản phẩm (kho hàng) | Product, Inventory, Warehouse |
| **Order** | Tạo đơn hàng, tính tổng tiền | Order, OrderItem |
| **Payment** | Tích hợp thanh toán MoMo | Payment |
| **Shipment** | Quản lý vận đơn, giao hàng | Shipment, ShipmentItem, ShipmentTrackingLog |
| **Delivery** | Ghi nhận các lần giao hàng | DeliveryAttempt |
| **ShippingFee** | Cấu hình phí vận chuyển theo tuyến | ShippingFee |

---

## 6. Công nghệ sử dụng

- **Framework**: Spring Boot 3.x (Java 21)
- **Database**: MySQL 8.0+ (JPA/Hibernate)
- **Cache**: Redis (Spring Cache + StringRedisTemplate)
- **Security**: Spring Security 6, JWT (jjwt 0.12.x)
- **Mapping**: MapStruct
- **API Docs**: SpringDoc OpenAPI (Swagger UI)
- **Build**: Maven
- **Container**: Docker, Docker Compose

---

## 7. Quy ước đặt tên

| Loại | Quy ước | Ví dụ |
|------|---------|-------|
| Domain Model | PascalCase, danh từ | `Account`, `OrderItem` |
| JPA Entity | PascalCase + Entity | `AccountEntity`, `OrderEntity` |
| Repository | Interface, ends with Repository | `AccountRepository` |
| Application Service | PascalCase + ApplicationService | `OrderApplicationService` |
| Domain Service | PascalCase + DomainService | `OrderDomainService` |
| REST Controller | PascalCase + Controller | `OrderController` |
| DTO Request | PascalCase + Request | `OrderRequest` |
| DTO Response | PascalCase + Response | `OrderResponse` |
| Mapper | PascalCase + Mapper | `AccountMapper` |
