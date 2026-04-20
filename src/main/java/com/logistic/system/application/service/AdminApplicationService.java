// package com.logistic.system.application.service;

// import org.springframework.data.domain.Page;
// import org.springframework.stereotype.Service;
// import lombok.RequiredArgsConstructor;
// import org.springframework.transaction.annotation.Transactional;

// import com.logistic.system.application.dto.response.StaffResponse;
// import com.logistic.system.domain.enums.AccountStatus;
// import com.logistic.system.infrastructure.persistence.entity.AccountEntity;
// import
// com.logistic.system.infrastructure.persistence.repository.AccountRepository;

// @Service
// @RequiredArgsConstructor
// public class AdminApplicationService {
// private final AccountRepository accountRepository;

// /**
// * Lấy danh sách tài khoản nhân viên chờ duyệt
// */
// public Page<StaffResponse> getPendingStaff() {

// }
// /**
// * Cập nhật trạng thái tài khoản nhân viên
// */
// @Transactional
// public void updateStaffStatus(Long staffId, AccountStatus status) {
// AccountEntity account = accountRepository.findById(staffId)
// .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản nhân
// viên"));

// account.setStatus(status);
// accountRepository.save(account);
// }
// /**
// * Cập nhật trạng thái tài khoản khách hàng
// */
// @Transactional
// public void updateCustomerStatus(Long customerId, AccountStatus status) {
// AccountEntity account = accountRepository.findById(customerId)
// .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản nhân
// viên"));

// account.setStatus(status);
// accountRepository.save(account);
// }
// }
