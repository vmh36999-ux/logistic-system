package com.logistic.system.infrastructure.security;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class BlacklistService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Đưa Token vào danh sách đen trong Redis
     * 
     * @param token       Chuỗi JWT cần chặn
     * @param ttlInMillis Thời gian sống còn lại của Token (mili giây)
     */
    public void addToBlacklist(String token, long ttlInMillis) {
        if (ttlInMillis > 0) {
            // Key sẽ có dạng: "blacklist:eyJhbG..."
            String key = "blacklist:" + token;
            // Lưu vào Redis: key, value ("true"), thời gian, đơn vị thời gian
            redisTemplate.opsForValue().set(key, "true", ttlInMillis, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Kiểm tra xem Token có nằm trong danh sách đen không
     * 
     * @param token Chuỗi JWT gửi lên từ Request
     * @return true nếu đã logout, false nếu vẫn hợp lệ
     */
    public boolean isTokenBlacklisted(String token) {
        String key = "blacklist:" + token;
        // Kiểm tra sự tồn tại của key trong Redis
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
