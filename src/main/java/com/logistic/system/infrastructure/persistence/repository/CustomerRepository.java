package com.logistic.system.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistic.system.infrastructure.persistence.entity.CustomerEntity;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByAccount_AccountId(Long accountId);

    Optional<CustomerEntity> findByCustomerCode(String customerCode);

    // Trong CustomerRepository.java
    // Đổi từ: findByAccountIdAndCustomerCode
    // Thành:
    // Trong CustomerRepository.java
    // Đổi từ: findByAccountIdAndCustomerCode
    // Thành:
    Optional<CustomerEntity> findByAccount_AccountIdAndCustomerCode(Long accountId, String customerCode);

    // CustomerEntity findByAccount_AccountIdAndCustomerCodeAndStatus(Long
    // accountId, String customerCode,
    // AccountStatus status);

}
