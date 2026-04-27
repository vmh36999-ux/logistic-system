package com.logistic.system.domain.service;

import org.springframework.stereotype.Service;

import com.logistic.system.domain.enums.AccountRole;
import com.logistic.system.domain.enums.AccountStatus;
import com.logistic.system.domain.model.Account;

@Service
public class AccountDomainService {
    public void validate(Account account) {
        if (account.getStatus().equals(AccountStatus.LOCKED)) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }
        if (!account.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new RuntimeException("Chỉ tài khoản đang hoạt động mới được phép khóa");
        }
        if (account.getRole().equals(AccountRole.ADMIN)) {
            throw new RuntimeException("Không được khóa tài khoản ADMIN");
        }

    }
}
