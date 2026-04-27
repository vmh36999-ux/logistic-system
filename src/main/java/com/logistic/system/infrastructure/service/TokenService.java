package com.logistic.system.infrastructure.service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String createRefreshToken(Long userId) {
        String refreshToken = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(
                "RT:" + refreshToken,
                userId.toString(),
                7,
                TimeUnit.DAYS);

        return refreshToken;
    }

    public void blacklistOldSession(String oldJti, long ttl) {
        if (oldJti != null && ttl > 0) {
            stringRedisTemplate.opsForValue().set(
                    "BL:" + oldJti,
                    "1",
                    ttl,
                    TimeUnit.MILLISECONDS);
        }
    }

    // 1. Lưu theo cấu trúc RT:userId:uuid để dễ quản lý theo User
    public void saveRefreshToken(Long userId, String refreshToken) {
        // Key này giúp bạn xóa tất cả token của 1 user cực nhanh
        String key = "RT:" + userId + ":" + refreshToken;
        stringRedisTemplate.opsForValue().set(key, "active", 7, TimeUnit.DAYS);
    }

    public String getActiveJti(Long userId) {
        return stringRedisTemplate.opsForValue().get("ActiveJti:" + userId);
    }

    // 2. Blacklist JTI
    public void addToBlacklist(String jti, long expirationMs) {
        if (expirationMs > 0) {
            stringRedisTemplate.opsForValue().set("Blacklist:" + jti, "LOCKED", expirationMs, TimeUnit.MILLISECONDS);
        }
    }

    // Lưu JTI đang hoạt động của User vào Redis (Hết hạn cùng Access Token)
    public void saveActiveJti(Long userId, String jti, long expirationMs) {
        if (expirationMs > 0) {
            stringRedisTemplate.opsForValue().set("ActiveJti:" + userId + ":" + jti, "active", expirationMs,
                    TimeUnit.MILLISECONDS);
        }
    }

    public boolean isBlocked(Long userId) {
        return Boolean.TRUE.equals(
                stringRedisTemplate.hasKey("BLOCK:" + userId));
    }

    // Kiểm tra Access Token có trong blacklist hay không
    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey("Blacklist:" + jti));
    }

    // 1 Staff chủ động Logout (Đã có sẵn chuỗi token)
    public void deleteRefreshToken(String refreshToken) {
        Set<String> keys = stringRedisTemplate.keys("RT:" + refreshToken);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    // 2 Admin khóa tài khoản
    public void deleteRefreshTokenByUser(Long accountId) {
        // Xóa đên mọi thiết bị của staff
        String pattern = "RT:" + accountId + "*";
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }
}
