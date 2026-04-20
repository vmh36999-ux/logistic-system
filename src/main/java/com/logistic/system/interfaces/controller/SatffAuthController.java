package com.logistic.system.interfaces.controller;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.request.LoginRequest;
import com.logistic.system.application.dto.request.StaffRegisterRequest;
import com.logistic.system.application.dto.response.AuthResponse;
import com.logistic.system.application.service.StaffAuthApplicationService;
import com.logistic.system.infrastructure.security.BlacklistService;
import com.logistic.system.infrastructure.security.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth/staff")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "StaffAuthentication", description = "Các API cho nhân viên (Đăng nhập, Đăng ký, Đăng xuất)")
public class SatffAuthController {
    private final StaffAuthApplicationService staffauthApplicationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BlacklistService blacklistService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody StaffRegisterRequest request) {
        AuthResponse response = staffauthApplicationService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = staffauthApplicationService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String jwt = jwtTokenProvider.parseJwt(request);
            if (jwt == null)
                return ResponseEntity.badRequest().body("Token missing!");
            // Kiểm tra xem có hợp lệ hay không
            if (!jwtTokenProvider.validateToken(jwt)) {
                return ResponseEntity.badRequest().body("Token không hợp lệ!");
            }
            Date expriryDate = jwtTokenProvider.getExpirationDateFromToken(jwt);
            Long ttl = expriryDate.getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                blacklistService.addToBlacklist(jwt, ttl);
                System.out.println("Logout thành công!");
            }
            // Logout vẫn thành công trong mọi trường hợp token hợp lệ
            return ResponseEntity.ok("Đăng xuất thành công!");
        } catch (ExpiredJwtException e) {
            // Token hết hạn vẫn coi như logout thành công
            return ResponseEntity.ok("Đăng xuất thành công (token đã hết hạn)!");
        } catch (Exception e) {
            log.error("Logout error " + e);
            return ResponseEntity.badRequest().body("Đăng xuất không thành công!");
        }
    }
}
