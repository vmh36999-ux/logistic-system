package com.logistic.system.application.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.application.dto.request.LoginRequest;
import com.logistic.system.application.dto.request.StaffRegisterRequest;
import com.logistic.system.application.dto.response.AuthResponse;
import com.logistic.system.domain.enums.AccountRole;
import com.logistic.system.domain.enums.AccountStatus;
import com.logistic.system.domain.model.Account;
import com.logistic.system.infrastructure.mapper.AccountMapper;
import com.logistic.system.infrastructure.persistence.entity.AccountEntity;
import com.logistic.system.infrastructure.persistence.entity.StaffEntity;
import com.logistic.system.infrastructure.persistence.repository.AccountRepository;
import com.logistic.system.infrastructure.persistence.repository.StaffRepository;
import com.logistic.system.infrastructure.security.JwtTokenProvider;
import com.logistic.system.infrastructure.service.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class StaffAuthApplicationService {

        private final AuthenticationManager authenticationManager;
        private final JwtTokenProvider tokenProvider;
        private final AccountRepository accountRepository;
        private final StaffRepository staffRepository;
        private final AccountMapper accountMapper;
        private final PasswordEncoder passwordEncoder;
        private final TokenService tokenService;
        private final StringRedisTemplate stringRedisTemplate;

        /**
         * Use case: xử lý login
         */
        public AuthResponse login(LoginRequest request) {
                try {
                        // Bước 1: Xác thực thông tin đăng nhập
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(request.getUsername(),
                                                        request.getPassword()));

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                        // Repository trả về AccountEntity
                        AccountEntity accountEntity = accountRepository.findByEmail(userDetails.getUsername())
                                        .or(() -> accountRepository.findByPhone(userDetails.getUsername()))
                                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản"));

                        Long accountId = accountEntity.getAccountId();
                        // chặn
                        if (Boolean.TRUE.equals(
                                        stringRedisTemplate.hasKey("BLOCK:" + accountId))) {
                                throw new RuntimeException("Tài khoản đã bị khóa");
                        }
                        // tạo token mới
                        String jti = UUID.randomUUID().toString();
                        String accessToken = tokenProvider.generateToken(authentication, jti);
                        long ttl = tokenProvider.getRemainingTime(accessToken);
                        // 2. lấy session cũ từ Redis
                        String oldJti = stringRedisTemplate.opsForValue().get("ActiveJti:" + accountId);

                        if (oldJti != null) {
                                tokenService.addToBlacklist(oldJti, ttl);
                        }

                        // 3. lưu session mới
                        stringRedisTemplate.opsForValue().set(
                                        "ActiveJti:" + accountId,
                                        jti,
                                        ttl,
                                        TimeUnit.MILLISECONDS);

                        // 4. refresh token qua service
                        String refreshToken = tokenService.createRefreshToken(accountId);

                        // Mapper chuyển sang domain model
                        Account account = accountMapper.toDomain(accountEntity);

                        // Bước 5: Trả về AuthResponse
                        return AuthResponse.builder()
                                        .accessToken(accessToken)
                                        .refreshToken(refreshToken)
                                        .username(userDetails.getUsername())
                                        .role(account.getRole().name())
                                        .tokenType("Bearer")
                                        .build();
                } catch (BadCredentialsException e) {
                        throw new RuntimeException("Sai tên đăng nhập hoặc mật khẩu");
                }
        }

        /**
         * Use case: xử lý register
         */
        @Transactional
        public AuthResponse register(StaffRegisterRequest request) {
                // 1. Kiểm tra tồn tại
                if (accountRepository.existsByEmail(request.getEmail())) {
                        throw new RuntimeException("Email đã tồn tại");
                }
                if (accountRepository.existsByPhone(request.getPhone())) {
                        throw new RuntimeException("Số điện thoại đã tồn tại");
                }
                // Kiểm tra warehouse có thật không (Chặn data ảo)

                // 2. Tạo AccountEntity
                AccountEntity accountEntity = AccountEntity.builder()
                                .email(request.getEmail())
                                .phone(request.getPhone())
                                .passwordHash(passwordEncoder.encode(request.getPassword()))
                                .role(AccountRole.STAFF)
                                .status(AccountStatus.ACTIVE)
                                .build();

                accountEntity = accountRepository.save(accountEntity);

                // 3. Tạo StaffEntity
                StaffEntity staffEntity = StaffEntity.builder()
                                .account(accountEntity)
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .birthDate(request.getBirthDate())
                                .address(request.getAddress())
                                // .province(provinceRepository.findById(request.getProvinceId())
                                // .orElseThrow(() -> new RuntimeException(
                                // "Tĩnh/Thành phố không tồn tại")))
                                // .district(districtRepository.findById(request.getDistrictId())
                                // .orElseThrow(() -> new RuntimeException("Quận/Huyện không tồn tại")))
                                // .ward(wardRepository.findById(request.getWardId())
                                // .orElseThrow(() -> new RuntimeException("Phường/Xã không tồn tại")))
                                .build();
                staffRepository.save(staffEntity);

                // 4. Tự động login sau khi đăng ký thành công
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setUsername(request.getEmail());
                loginRequest.setPassword(request.getPassword());

                return login(loginRequest);
        }

        /**
         * Use case: xử lý refresh token
         */
        public AuthResponse refresh(String refreshToken) {

                String userId = stringRedisTemplate.opsForValue()
                                .get("RT:" + refreshToken);

                if (userId == null) {
                        throw new RuntimeException("Refresh token invalid");
                }

                if (Boolean.TRUE.equals(
                                stringRedisTemplate.hasKey("BLOCK:" + userId))) {
                        throw new RuntimeException("User blocked");
                }

                Long id = Long.parseLong(userId);

                AccountEntity account = accountRepository.findById(id)
                                .orElseThrow();

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                account.getEmail(),
                                null,
                                List.of());

                String jti = UUID.randomUUID().toString();
                String newAccessToken = tokenProvider.generateToken(authentication, jti);

                long ttl = tokenProvider.getRemainingTime(newAccessToken);

                // overwrite session (1 device)
                stringRedisTemplate.opsForValue().set(
                                "ActiveSession:" + userId,
                                newAccessToken,
                                ttl,
                                TimeUnit.MILLISECONDS);

                return AuthResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(refreshToken)
                                .build();
        }

}