package com.logistic.system.infrastructure.mapper;

import com.logistic.system.domain.model.Account;
import com.logistic.system.infrastructure.persistence.entity.AccountEntity;
import com.logistic.system.application.dto.response.AuthResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    // Entity ↔ Domain
    Account toDomain(AccountEntity entity);

    AccountEntity toEntity(Account domain);

    // Domain ↔ DTO

    @Mapping(source = "email", target = "username") // ví dụ lấy username từ email
    @Mapping(target = "accessToken", ignore = true) // token sinh ở AuthService, không map ở đây
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(source = "role", target = "role")
    AuthResponse toAuthResponse(Account domain);
}