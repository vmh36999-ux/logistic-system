package com.logistic.system.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Service
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt.secret:logistics_system_secret_key_2024_auth_key_very_long_secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:3600000}")
    private long jwtExpirationInMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate JWT token from Authentication info thử satefull
     */
    // public String generateToken(UserDetails userDetails, String jti) {
    // Map<String, Object> claims = new HashMap<>();
    // // có thể thêm thông tin khác
    // return Jwts.builder()
    // .setClaims(claims)
    // .setSubject(userDetails.getUsername()) // Lưu username
    // .setId(jti) // ĐÂY LÀ DÒNG QUAN TRỌNG NHẤT: Gán mã định danh JTI
    // .setIssuedAt(new Date(System.currentTimeMillis()))
    // .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
    // .signWith(getSigningKey()) // Ký tên bằng Secret Key
    // .compact();

    // }
    public String generateToken(Authentication authentication, String jti) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .setId(jti) // thêm JTI vào JWT
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Get username from JWT
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Get user id from JWT
     */
    public Long getUserIdFromJWT(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Long.parseLong(claims.get("accountId").toString());

        } catch (Exception e) {
            log.error("Không thể lấy UserId từ Token: {}", e.getMessage());
            return null; // Trả về null thay vì quăng RuntimeException
        }
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Log error if needed
        }
        return false;
    }

    public Date getExpirationDateFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    /**
     * Tính thời gian còn lại (TTL) của access token để:
     * blacklist đúng thời gian
     * lưu Redis đúng expire
     */
    public long getRemainingTime(String token) {
        Date expiration = getExpirationDateFromToken(token);

        long remainingTime = expiration.getTime() - System.currentTimeMillis();

        return Math.max(remainingTime, 0);
    }

    public String getJtiFromJWT(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getId();
    }

    public String parseJwt(HttpServletRequest request) {
        // 1. Lấy chuỗi từ Header "Authorization"
        String headerAuth = request.getHeader("Authorization");

        // 2. Kiểm tra Header có tồn tại và bắt đầu bằng "Bearer " không
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            // 3. Cắt bỏ 7 ký tự đầu ("Bearer ") để lấy nguyên chuỗi Token
            return headerAuth.substring(7);
        }

        return null;
    }

}
