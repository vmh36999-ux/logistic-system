package com.logistic.system.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ward {
    private Long wardId;
    private Long districtId;
    private String code;
    private String name;
    private String nameEn;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
