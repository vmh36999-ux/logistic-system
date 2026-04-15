package com.logistic.system.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Province implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long provinceId;
    private String code;
    private String name;
    private String nameEn;
    private String region;
    private Integer priority;
    private String areaCode;
    private String postalCode;
    private BigDecimal area;
    private Long population;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<District> districts;
}
