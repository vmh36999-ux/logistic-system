package com.logistic.system.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistic.system.infrastructure.persistence.entity.ProvinceEntity;

@Repository
public interface ProvinceRepository extends JpaRepository<ProvinceEntity, Long> {

    @Cacheable(value = "provinces")
    List<ProvinceEntity> findAll();

    @Override
    @CacheEvict(value = "provinces", allEntries = true)
    <S extends ProvinceEntity> S save(S entity);
}
