package com.logistic.system.domain.service;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.enums.AccountStatus;
import com.logistic.system.domain.model.Account;

/**
 * Domain Service cho Authentication
 * Chứa logic nghiệp vụ thuần, không phụ thuộc Spring/Security framework.
 */
@Service
public class AuthDomainService {

    /**
     * Kiểm tra xem account có hợp lệ để đăng nhập hay không
     */
    public void validateAccountStatus(Account account) {
        if (account == null) {
            throw new RuntimeException("Tài khoản không tồn tại");
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Tài khoản đang bị " + account.getStatus().getLabel());
        }
    }

    /**
     * Lấy tên Role dưới dạng String để trả về cho Client
     */
    public String getRoleName(Account account) {
        if (account.getRole() == null) {
            return "UNKNOWN";
        }
        return account.getRole().name();
    }
}
