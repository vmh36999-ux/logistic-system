-- =============================================================================
-- SQL DDL for Logistics System (MySQL 8.0+)
-- Generated from ALL Entity classes (Final Thorough Version)
-- =============================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Table: provinces
CREATE TABLE IF NOT EXISTS `provinces` (
    `province_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(20) NOT NULL UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `name_en` VARCHAR(100),
    `region` VARCHAR(50),
    `priority` INTEGER DEFAULT 0,
    `area_code` VARCHAR(10),
    `postal_code` VARCHAR(10),
    `area` DECIMAL(10, 2),
    `population` BIGINT,
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    INDEX `idx_province_code` (`code`),
    INDEX `idx_province_region` (`region`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Table: districts
CREATE TABLE IF NOT EXISTS `districts` (
    `district_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `province_id` BIGINT NOT NULL,
    `code` VARCHAR(20) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `name_en` VARCHAR(100),
    `type` VARCHAR(20),
    `postal_code` VARCHAR(10),
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    INDEX `idx_district_province` (`province_id`),
    INDEX `idx_district_code` (`code`),
    CONSTRAINT `fk_district_province` FOREIGN KEY (`province_id`) REFERENCES `provinces` (`province_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Table: wards
CREATE TABLE IF NOT EXISTS `wards` (
    `ward_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `district_id` BIGINT NOT NULL,
    `code` VARCHAR(20) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `name_en` VARCHAR(100),
    `type` VARCHAR(20),
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    INDEX `idx_ward_district` (`district_id`),
    INDEX `idx_ward_code` (`code`),
    INDEX `idx_ward_district_code` (`district_id`, `code`),
    CONSTRAINT `fk_ward_district` FOREIGN KEY (`district_id`) REFERENCES `districts` (`district_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Table: accounts
CREATE TABLE IF NOT EXISTS `accounts` (
    `account_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(100) UNIQUE,
    `phone` VARCHAR(15) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` VARCHAR(20) NOT NULL,
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    INDEX `idx_auth_phone` (`phone`),
    INDEX `idx_auth_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Table: customers
CREATE TABLE IF NOT EXISTS `customers` (
    `customer_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `account_id` BIGINT NOT NULL UNIQUE,
    `customer_code` VARCHAR(50) UNIQUE,
    `first_name` VARCHAR(100) NOT NULL,
    `last_name` VARCHAR(100) NOT NULL,
    `province_id` BIGINT,
    `district_id` BIGINT,
    `ward_id` BIGINT,
    `default_address` TEXT,
    `gender` VARCHAR(10),
    `birth_date` DATE,
    `loyalty_points` INTEGER DEFAULT 0,
    `total_spent` DECIMAL(15, 2) DEFAULT 0.00,
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    INDEX `idx_customer_account` (`account_id`),
    INDEX `idx_customer_code` (`customer_code`),
    CONSTRAINT `fk_customer_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`),
    CONSTRAINT `fk_customer_province` FOREIGN KEY (`province_id`) REFERENCES `provinces` (`province_id`),
    CONSTRAINT `fk_customer_district` FOREIGN KEY (`district_id`) REFERENCES `districts` (`district_id`),
    CONSTRAINT `fk_customer_ward` FOREIGN KEY (`ward_id`) REFERENCES `wards` (`ward_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Table: products
CREATE TABLE IF NOT EXISTS `products` (
    `product_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `sku` VARCHAR(50) NOT NULL UNIQUE,
    `name` VARCHAR(255) NOT NULL,
    `base_price` DECIMAL(15, 2) NOT NULL,
    `weight_gram` DECIMAL(10, 2) NOT NULL,
    `description` TEXT,
    `category_id` BIGINT,
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Table: warehouses
CREATE TABLE IF NOT EXISTS `warehouses` (
    `warehouse_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(20) NOT NULL UNIQUE,
    `name` VARCHAR(100),
    `address` TEXT,
    `province_id` BIGINT NOT NULL,
    `district_id` BIGINT NOT NULL,
    `ward_id` BIGINT NOT NULL,
    `type` VARCHAR(20) DEFAULT 'HUB',
    `priority` INTEGER DEFAULT 1,
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    CONSTRAINT `fk_warehouse_province` FOREIGN KEY (`province_id`) REFERENCES `provinces` (`province_id`),
    CONSTRAINT `fk_warehouse_district` FOREIGN KEY (`district_id`) REFERENCES `districts` (`district_id`),
    CONSTRAINT `fk_warehouse_ward` FOREIGN KEY (`ward_id`) REFERENCES `wards` (`ward_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Table: staff
CREATE TABLE IF NOT EXISTS `staff` (
    `staff_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `account_id` BIGINT NOT NULL UNIQUE,
    `code` VARCHAR(50) NOT NULL UNIQUE,
    `first_name` VARCHAR(100) NOT NULL,
    `last_name` VARCHAR(100) NOT NULL,
    `warehouse_id` BIGINT,
    `address` TEXT,
    `birth_date` DATE,
    `start_date` DATE,
    `note` TEXT,
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    INDEX `idx_staff_code` (`code`),
    INDEX `idx_staff_account` (`account_id`),
    CONSTRAINT `fk_staff_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`),
    CONSTRAINT `fk_staff_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Table: orders
CREATE TABLE IF NOT EXISTS `orders` (
    `order_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_code` VARCHAR(50) NOT NULL UNIQUE,
    `customer_id` BIGINT NOT NULL,
    `receiver_name` VARCHAR(100) NOT NULL,
    `receiver_phone` VARCHAR(15) NOT NULL,
    `receiver_province_id` BIGINT NOT NULL,
    `receiver_address` TEXT NOT NULL,
    `total_amount` DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    `payment_method` VARCHAR(50),
    `payment_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `order_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `note` TEXT,
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    CONSTRAINT `fk_order_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    CONSTRAINT `fk_order_province` FOREIGN KEY (`receiver_province_id`) REFERENCES `provinces` (`province_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Table: order_items
CREATE TABLE IF NOT EXISTS `order_items` (
    `order_item_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INTEGER NOT NULL,
    `price_at_purchase` DECIMAL(15, 2) NOT NULL,
    `weight_gram` DECIMAL(10, 2) NOT NULL,
    `discount` DECIMAL(5, 2) DEFAULT 0.00,
    `sub_total` DECIMAL(15, 2) NOT NULL,
    `note` TEXT,
    CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
    CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Table: payments
CREATE TABLE IF NOT EXISTS `payments` (
    `payment_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL,
    `amount` DECIMAL(19, 2) NOT NULL,
    `currency` VARCHAR(10) DEFAULT 'VND',
    `payment_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `request_id` VARCHAR(100) UNIQUE,
    `trans_id` VARCHAR(100),
    `pay_url` VARCHAR(500),
    `payment_method` VARCHAR(50),
    `raw_response` TEXT,
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    `paid_at` DATETIME(6),
    `expired_at` DATETIME(6),
    INDEX `idx_payment_request_id` (`request_id`),
    INDEX `idx_payment_trans_id` (`trans_id`),
    CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Table: shipments
CREATE TABLE IF NOT EXISTS `shipments` (
    `shipment_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL UNIQUE,
    `tracking_number` VARCHAR(50) NOT NULL UNIQUE,
    `current_warehouse_id` BIGINT,
    `receiver_name` VARCHAR(100) NOT NULL,
    `receiver_phone` VARCHAR(15) NOT NULL,
    `receiver_province_id` BIGINT NOT NULL,
    `receiver_district_id` BIGINT,
    `receiver_ward_id` BIGINT,
    `delivery_address` TEXT NOT NULL,
    `total_weight` DECIMAL(10, 2),
    `shipping_fee` DECIMAL(15, 2),
    `shipment_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `expected_delivery_date` DATE,
    `delivered_at` DATETIME(6),
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    INDEX `idx_shipment_tracking` (`tracking_number`),
    INDEX `idx_shipment_status` (`shipment_status`),
    CONSTRAINT `fk_shipment_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
    CONSTRAINT `fk_shipment_warehouse` FOREIGN KEY (`current_warehouse_id`) REFERENCES `warehouses` (`warehouse_id`),
    CONSTRAINT `fk_shipment_province` FOREIGN KEY (`receiver_province_id`) REFERENCES `provinces` (`province_id`),
    CONSTRAINT `fk_shipment_district` FOREIGN KEY (`receiver_district_id`) REFERENCES `districts` (`district_id`),
    CONSTRAINT `fk_shipment_ward` FOREIGN KEY (`receiver_ward_id`) REFERENCES `wards` (`ward_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. Table: shipment_items
CREATE TABLE IF NOT EXISTS `shipment_items` (
    `shipment_item_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `shipment_id` BIGINT NOT NULL,
    `order_item_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INTEGER NOT NULL,
    `picked_quantity` INTEGER DEFAULT 0,
    `picked_at` DATETIME(6),
    `packed_quantity` INTEGER DEFAULT 0,
    `packed_at?` DATETIME(6), -- Fixed from packed_at
    `note` TEXT,
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    UNIQUE KEY `idx_shipment_item_unique` (`shipment_id`, `order_item_id`),
    CONSTRAINT `fk_shipment_item_shipment` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`shipment_id`),
    CONSTRAINT `fk_shipment_item_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. Table: shipment_tracking_logs
CREATE TABLE IF NOT EXISTS `shipment_tracking_logs` (
    `log_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `shipment_id` BIGINT NOT NULL,
    `status` VARCHAR(50) NOT NULL,
    `status_code` VARCHAR(20),
    `location` VARCHAR(255),
    `description` TEXT,
    `updated_by` VARCHAR(100),
    `estimated_next_time` DATETIME(6),
    `created_at` DATETIME(6),
    INDEX `idx_tracking_shipment` (`shipment_id`),
    INDEX `idx_tracking_created` (`created_at`),
    CONSTRAINT `fk_tracking_shipment` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`shipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. Table: staff (Correction: Handled above at #8)

-- 16. Table: delivery_attempts
CREATE TABLE IF NOT EXISTS `delivery_attempts` (
    `attempt_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `shipment_id` BIGINT NOT NULL,
    `shipment_item_id` BIGINT,
    `staff_id` BIGINT,
    `attempt_number` INTEGER DEFAULT 1,
    `delivered_quantity` INTEGER DEFAULT 0,
    `failed_quantity` INTEGER DEFAULT 0,
    `status` VARCHAR(20),
    `reason` VARCHAR(255),
    `image_proof_url` VARCHAR(500),
    `signature_url` VARCHAR(500),
    `gps_latitude` DOUBLE,
    `gps_longitude` DOUBLE,
    `expected_time` DATETIME(6),
    `attempt_time` DATETIME(6),
    `note` TEXT,
    `created_at` DATETIME(6),
    INDEX `idx_delivery_shipment` (`shipment_id`),
    INDEX `idx_delivery_staff` (`staff_id`),
    CONSTRAINT `fk_delivery_shipment` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`shipment_id`),
    CONSTRAINT `fk_delivery_shipment_item` FOREIGN KEY (`shipment_item_id`) REFERENCES `shipment_items` (`shipment_item_id`),
    CONSTRAINT `fk_delivery_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. Table: inventory
CREATE TABLE IF NOT EXISTS `inventory` (
    `inventory_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT NOT NULL,
    `warehouse_id` BIGINT NOT NULL,
    `quantity` INTEGER DEFAULT 0,
    `safe_stock` INTEGER DEFAULT 0,
    `max_stock` INTEGER DEFAULT 0,
    `reorder_point` INTEGER DEFAULT 0,
    `location_rack` VARCHAR(50),
    `cost_price` DECIMAL(15, 2),
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    INDEX `idx_inventory_product` (`product_id`),
    INDEX `idx_inventory_warehouse` (`warehouse_id`),
    UNIQUE KEY `idx_inventory_product_warehouse` (`product_id`, `warehouse_id`),
    CONSTRAINT `fk_inventory_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
    CONSTRAINT `fk_inventory_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 18. Table: shipping_fees
CREATE TABLE IF NOT EXISTS `shipping_fees` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `from_province_id` BIGINT NOT NULL,
    `to_province_id` BIGINT NOT NULL,
    `weight_from` DECIMAL(10, 2),
    `weight_to` DECIMAL(10, 2),
    `base_fee` DECIMAL(15, 2) NOT NULL,
    `additional_fee_per_kg` DECIMAL(10, 2),
    `estimated_days` INTEGER,
    `created_at` DATETIME(6),
    INDEX `idx_shipping_fee_route` (`from_province_id`, `to_province_id`),
    CONSTRAINT `fk_shipping_fee_from` FOREIGN KEY (`from_province_id`) REFERENCES `provinces` (`province_id`),
    CONSTRAINT `fk_shipping_fee_to` FOREIGN KEY (`to_province_id`) REFERENCES `provinces` (`province_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
