package com.logistic.system.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistic.system.domain.enums.AccountStatus;
import com.logistic.system.infrastructure.persistence.entity.StaffEntity;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long> {
    Page<StaffEntity> findByAccount_Status(AccountStatus status, Pageable pageable);
}
