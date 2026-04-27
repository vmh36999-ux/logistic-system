package com.logistic.system.interfaces.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.request.LoginRequest;
import com.logistic.system.application.dto.request.RegisterRequest;
import com.logistic.system.application.dto.response.AuthResponse;
import com.logistic.system.application.service.AuthApplicationService;
import com.logistic.system.infrastructure.security.BlacklistService;
import com.logistic.system.infrastructure.security.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Các API xác thực người dùng (Đăng nhập, Đăng ký, Đăng xuất)")
public class AuthController {
    @Autowired
    private BlacklistService blacklistService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private final AuthApplicationService authApplicationService;

    @Operation(summary = "Đăng nhập", description = "Xác thực người dùng bằng username/password và trả về JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
            @ApiResponse(responseCode = "401", description = "Sai thông tin đăng nhập")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authApplicationService.login(request);

        return ResponseEntity.ok(response);
    }

    // Đăng ký dành cho Customer
    @Operation(summary = "Đăng ký tài khoản", description = "Tạo tài khoản mới cho khách hàng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng ký thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc email/số điện thoại đã tồn tại")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authApplicationService.register(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Đăng xuất", description = "Vô hiệu hóa token hiện tại bằng cách đưa vào blacklist")
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
