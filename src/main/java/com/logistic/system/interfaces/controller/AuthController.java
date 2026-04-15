package com.logistic.system.interfaces.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.request.RegisterRequest;
import com.logistic.system.application.service.AuthApplicationService;
import com.logistic.system.infrastructure.security.BlacklistService;
import com.logistic.system.infrastructure.security.JwtTokenProvider;
import com.logistic.system.interfaces.dto.request.LoginRequest;
import com.logistic.system.interfaces.dto.response.AuthResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private BlacklistService blacklistService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private final AuthApplicationService authApplicationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authApplicationService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authApplicationService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String jwt = jwtTokenProvider.parseJwt(request);
            if (jwt == null)
                return ResponseEntity.badRequest().body("Token missing!");

            // Validate token để lấy ngày hết hạn
            if (jwtTokenProvider.validateToken(jwt)) {
                Date expiryDate = jwtTokenProvider.getExpirationDateFromToken(jwt);
                long ttl = expiryDate.getTime() - System.currentTimeMillis();

                if (ttl > 0) {
                    blacklistService.addToBlacklist(jwt, ttl);
                    return ResponseEntity.ok("Logout thành công!");
                }
            }
        } catch (Exception e) {
            System.err.println("Logout Error: " + e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok("Token xử lý hoặc không hợp lệ, đăng xuất thành công!");
    }
}