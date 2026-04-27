package com.logistic.system.interfaces.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.response.StaffResponse;
import com.logistic.system.application.service.StaffApplicationService;
import com.logistic.system.domain.enums.AccountStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class StaffController {
    private final StaffApplicationService staffApplicationService;

    // Lấy danh sách staff chờ duyệt
    @GetMapping("/staff/status")
    public ResponseEntity<?> getStaffs(
            @RequestParam(required = false) AccountStatus status,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(staffApplicationService.listStaffs(status, pageable));
    }

    // Phê duyệt và cấp mã cho nhân viên
    @PatchMapping("/staff/{staffId}/approve")
    public ResponseEntity<StaffResponse> approveStaff(@PathVariable Long staffId) {
        StaffResponse response = staffApplicationService.approveStaff(staffId);
        return ResponseEntity.ok(response);
    }
    // // Cập nhật status của tài khoản
    // @PatchMapping("/staff/{staffId}/status")
    // public ResponseEntity<?> updateStaffStatus(
    // @PathVariable Long staffId,
    // @RequestParam AccountStatus status) {
    // adminApplicationService.updateStaffStatus(staffId, status);
    // return ResponseEntity.ok("Cập nhật trạng thái thành công!");

    // }

}
