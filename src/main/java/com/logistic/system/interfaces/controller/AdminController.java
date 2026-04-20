package com.logistic.system.interfaces.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    // private final AdminApplicationService adminApplicationService;
    // Lấy danh sách staff chờ duyệt
    // @GetMapping("/staff/pending")
    // public ResponseEntity<?> getPendingStaff() {
    // return ResponseEntity.ok(adminApplicationService.getPendingStaff());
    // }

    // // // Cập nhật status của tài khoản
    // // @PatchMapping("/staff/{staffId}/status")
    // // public ResponseEntity<?> updateStaffStatus(
    // // @PathVariable Long staffId,
    // // @RequestParam AccountStatus status) {
    // // adminApplicationService.updateStaffStatus(staffId, status);
    // // return ResponseEntity.ok("Cập nhật trạng thái thành công!");

    // // }

}
