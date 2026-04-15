package com.logistic.system.infrastructure.security;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.logistic.system.domain.enums.AccountRole;
import com.logistic.system.domain.enums.AccountStatus;
import com.logistic.system.infrastructure.persistence.entity.AccountEntity;
import com.logistic.system.infrastructure.persistence.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    // Inject giá trị từ file properties
    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        log.info(">>> Khởi tạo dữ liệu hệ thống...");

        if (accountRepository.findByEmail(adminEmail).isEmpty()) {
            AccountEntity admin = AccountEntity.builder()
                    .email(adminEmail)
                    .phone("0901112222")
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(AccountRole.ADMIN)
                    .status(AccountStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            accountRepository.save(admin);
            log.info(">>> SUCCESS: Đã tạo Admin từ cấu hình: {} / {}", adminEmail, adminPassword);
        } else {
            log.info(">>> SKIP: Tài khoản Admin đã tồn tại.");
        }
    }
}