package com.logistic.system.application.service;

import java.util.UUID;

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
import com.logistic.system.infrastructure.persistence.repository.DistrictRepository;
import com.logistic.system.infrastructure.persistence.repository.ProvinceRepository;
import com.logistic.system.infrastructure.persistence.repository.StaffRepository;
import com.logistic.system.infrastructure.persistence.repository.WardRepository;
import com.logistic.system.infrastructure.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class StaffAuthApplicationService {

        private final AuthenticationManager authenticationManager;
        private final JwtTokenProvider tokenProvider;
        private final AccountRepository accountRepository;
        private final StaffRepository staffRepository;
        private final ProvinceRepository provinceRepository;
        private final DistrictRepository districtRepository;
        private final WardRepository wardRepository;
        private final AccountMapper accountMapper;
        private final PasswordEncoder passwordEncoder;

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

                        // Bước 2: Sinh JWT token
                        String jwt = tokenProvider.generateToken(authentication);

                        // Bước 3: Lấy thông tin user
                        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                        // Repository trả về AccountEntity
                        AccountEntity accountEntity = accountRepository.findByEmail(userDetails.getUsername())
                                        .or(() -> accountRepository.findByPhone(userDetails.getUsername()))
                                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản"));

                        // Mapper chuyển sang domain model
                        Account account = accountMapper.toDomain(accountEntity);

                        // Bước 5: Trả về AuthResponse
                        return AuthResponse.builder()
                                        .accessToken(jwt)
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
                                .code("STAFF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
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
}