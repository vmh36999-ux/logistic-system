package com.logistic.system.infrastructure.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.infrastructure.persistence.entity.AccountEntity;
import com.logistic.system.infrastructure.persistence.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        // Hỗ trợ đăng nhập bằng cả email và số điện thoại
        AccountEntity account = accountRepository.findByEmail(emailOrPhone)
                .or(() -> accountRepository.findByPhone(emailOrPhone))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy tài khoản với email hoặc số điện thoại: " + emailOrPhone));
        // Kiểm tra trạng thái tài khoản chặn logic đăng nhập
        // switch (account.getStatus()) {
        // case PENDING_APPROVAL -> throw new DisabledException("Tài khoản đang chờ quản
        // trị phê duyệt");
        // case REJECTED -> throw new DisabledException("Yêu cầu đăng ký tài khoản của
        // bạn đã bị từ chối");
        // case LOCKED -> throw new LockedException("Tài khoản của bạn đã bị khóa");
        // case INACTIVE -> throw new DisabledException("Tài khoản của bạn đã ngừnd hoạt
        // động");
        // case ACTIVE -> {
        // }

        // }
        // active tiệp tục xử lý
        return new User(
                account.getEmail() != null ? account.getEmail() : account.getPhone(),
                account.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority(account.getRole().name())));
    }
}
