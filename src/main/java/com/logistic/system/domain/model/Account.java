package com.logistic.system.domain.model;

import com.logistic.system.domain.enums.AccountRole;
import com.logistic.system.domain.enums.AccountStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Long accountId;
    private String email;
    private String phone;
    private String passwordHash;
    private AccountRole role;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
