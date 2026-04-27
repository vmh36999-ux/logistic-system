package com.logistic.system.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.service.AccountApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountApplicationService accountApplicationService;

    // Khóa tài khoản và đẩy Token vào Redis Blacklist
    @PatchMapping("/{accountId}/block")
    public ResponseEntity<?> blockAccount(@PathVariable Long accountId) {
        accountApplicationService.blockAccount(accountId);
        return ResponseEntity.ok("Tài khoản đã bị khóa!");
    }
}
