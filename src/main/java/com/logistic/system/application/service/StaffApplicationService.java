package com.logistic.system.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.logistic.system.application.dto.response.StaffResponse;
import com.logistic.system.domain.enums.AccountStatus;
import com.logistic.system.domain.model.Staff;
import com.logistic.system.domain.service.StaffDomainService;
import com.logistic.system.infrastructure.mapper.StaffMapper;
import com.logistic.system.infrastructure.persistence.entity.StaffEntity;
import com.logistic.system.infrastructure.persistence.repository.StaffRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffApplicationService {
    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;
    private final StaffDomainService staffDomainService;

    /**
     * Lấy danh sách nhân viên chờ duyệt
     */

    public Page<StaffResponse> listStaffs(AccountStatus status, Pageable pageable) {
        return staffRepository.findByAccount_Status(status, pageable)
                .map(entity -> {
                    Staff domain = staffMapper.toDomain(entity);
                    return staffMapper.toResponse(domain);
                });
    }

    /**
     * Phê duyệt và cấp mã cho nhân viên
     */
    public StaffResponse approveStaff(Long staffId) {
        StaffEntity staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        // chuyển Entity sang Domain
        Staff domain = staffMapper.toDomain(staff);
        // thực hiện nghiệp vụ
        staffDomainService.approve(domain);
        staffMapper.updateEntityFromDomain(domain, staff);
        // Save
        staffRepository.save(staffMapper.toEntity(domain));
        return staffMapper.toResponse(domain);
    }
    /**
     * Cập nhật trạng thái tài khoản nhân viên
     */
    // @Transactional
    // public void updateStaffStatus(Long staffId, AccountStatus status) {
    // AccountEntity account = accountRepository.findById(staffId)
    // .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản nhân
    // viên"));

    // account.setStatus(status);
    // accountRepository.save(account);
    // }

    /**
     * Cập nhật trạng thái tài khoản khách hàng
     */
    // @Transactional
    // public void updateCustomerStatus(Long customerId, AccountStatus status) {
    // AccountEntity account = accountRepository.findById(customerId)
    // .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản nhân
    // viên"));

    // account.setStatus(status);
    // accountRepository.save(account);
    // }
}
