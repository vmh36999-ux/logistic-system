package com.logistic.system.application.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.domain.model.Account;
import com.logistic.system.domain.service.AccountDomainService;
import com.logistic.system.infrastructure.mapper.AccountMapper;
import com.logistic.system.infrastructure.persistence.entity.AccountEntity;
import com.logistic.system.infrastructure.persistence.repository.AccountRepository;
import com.logistic.system.infrastructure.security.JwtTokenProvider;
import com.logistic.system.infrastructure.service.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountApplicationService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountDomainService accountDomainService;
    private final TokenService tokenService;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public void blockAccount(Long accountId) {
        AccountEntity entity = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        Account domain = accountMapper.toDomain(entity);
        accountDomainService.validate(domain);
        domain.block();
        entity.setStatus(domain.getStatus());
        // 1. mark blocked
        stringRedisTemplate.opsForValue().set("BLOCK:" + accountId, "1");

        // 2. get current session
        String accessToken = stringRedisTemplate.opsForValue()
                .get("ActiveSession:" + entity.getAccountId());

        if (accessToken != null) {

            String jti = tokenProvider.getJtiFromJWT(accessToken);
            long ttl = tokenProvider.getRemainingTime(accessToken);

            tokenService.addToBlacklist(jti, ttl);
        }

        // 3. remove session
        stringRedisTemplate.delete("ActiveSession:" + entity.getAccountId());

        // 4. remove refresh token
        tokenService.deleteRefreshTokenByUser(entity.getAccountId());
    }
}
