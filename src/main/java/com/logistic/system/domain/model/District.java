package com.logistic.system.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class District {
    private Long districtId;
    private Long provinceId;
    private String code;
    private String name;
    private String nameEn;
    private String type;
    private String postalCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Ward> wards;
}
