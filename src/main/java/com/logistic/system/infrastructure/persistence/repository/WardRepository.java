package com.logistic.system.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistic.system.infrastructure.persistence.entity.WardEntity;

@Repository
public interface WardRepository extends JpaRepository<WardEntity, Long> {
}
