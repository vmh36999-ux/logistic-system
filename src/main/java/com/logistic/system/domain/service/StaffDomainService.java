package com.logistic.system.domain.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.enums.AccountStatus;
import com.logistic.system.domain.model.Staff;
import com.logistic.system.infrastructure.persistence.repository.WarehouseRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffDomainService {
    private final WarehouseRepository warehouseRepository;

    /**
     * Kiểm tra tính hợp lệ về mặt nghiệp vụ của Staff trước khi lưu
     */
    public void validateInfor(Staff staff) {
        // 1. Nhân viên phải đủ 18 tuổi
        if (staff.getBirthDate() != null && staff.getBirthDate().plusYears(18).isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Nhân viên phải đủ 18 tuổi");
        }
        // 2. kiểm tra sựu tồn tại của kho
        if (staff.getWarehouse() == null || !warehouseRepository.existsById(staff.getWarehouse().getWarehouseId())) {
            throw new IllegalArgumentException("Kho không tồn tại");
        }
    }

    /**
     * Thực hiện các thay đổi nghiệp vụ khi một Staff được duyệt
     */
    // Logic khi duyệt nhân viên
    @Transactional
    public void approve(Staff staff) {
        if (staff.getAccount().getStatus() != AccountStatus.PENDING) {
            throw new IllegalArgumentException("Chỉ có thể duyệt tài khoản đang chờ.");
        }
        staff.getAccount().setStatus(AccountStatus.ACTIVE);
        // Sinh mã nhân viên chính thức
        staff.setCode("STAFF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
    /**
     * Từ chối kèm lý do
     */
}
