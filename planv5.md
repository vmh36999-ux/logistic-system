# Logistics System Implementation Plan (v5)

## 1. Project Scan & Current Status
- **Architecture**: Following Clean Architecture (Interfaces -> Application -> Domain -> Infrastructure).
- **Core Entities**: Account, Customer, Order, OrderItem, Product, Shipment, ShipmentItem, Warehouse, Inventory, Province, District, Ward, ShipmentTrackingLog, DeliveryAttempt, Staff, ShippingFee.
- **Repositories**: Most basic CRUD repositories are implemented.
- **Services**: Application and Domain services are partially implemented.
- **Mappers**: MapStruct is used for conversion between layers.

## 2. Issues & Improvements (Immediate Focus)
- **Fix Typo**: `application.dto.reponse` -> `application.dto.response` in all files.
- **Standardize DTOs**: Remove redundant DTOs in `interfaces/dto` and use `application/dto` consistently.
- **Inventory Integration**: Connect `OrderApplicationService` with `InventoryApplicationService` to check and deduct stock during order placement.
- **Price Management**: Retrieve actual product prices in `OrderApplicationService` instead of hardcoding.
- **Shipping Fee Calculation**: Implement dynamic shipping fee calculation based on weight, distance, and province.

## 3. Missing Components (Next Steps)
### 3.1. Repositories
- [ ] Create `ProvinceRepository`, `DistrictRepository`, `WardRepository`.
- [ ] Create `StaffRepository`.
- [ ] Create `ShipmentTrackingLogRepository`.
- [ ] Create `DeliveryAttemptRepository`.

### 3.2. Services & Controllers
- [ ] **Warehouse Management**:
    - [ ] `WarehouseDomainService`: Logic for capacity check, status management.
    - [ ] `WarehouseApplicationService`: CRUD operations, stock movement.
    - [ ] `WarehouseController`: API for warehouse operations.
- [ ] **Staff Management**:
    - [ ] `StaffApplicationService`: Manage warehouse staff and drivers.
    - [ ] `StaffController`: API for staff operations.
- [ ] **Location Management**:
    - [ ] `LocationApplicationService`: Retrieve provinces, districts, and wards.
    - [ ] `LocationController`: API for address selection.

## 4. Feature Completion
- [ ] **Order Lifecycle**: Implement status transitions (PENDING -> APPROVED -> PROCESSING -> SHIPPED -> DELIVERED).
- [ ] **Shipment Tracking**: Automatic creation of `ShipmentTrackingLog` when shipment status changes.
- [ ] **Inventory History**: Track stock movements (IN/OUT) for auditing.
- [ ] **Authentication & Security**: Complete JWT integration and role-based access control (RBAC) in controllers.

## 5. Technical Debt
- [ ] Fix MapStruct mapping issues (unmapped properties).
- [ ] Standardize error handling with a GlobalExceptionHandler.
- [ ] Add basic unit tests for Domain Services.
