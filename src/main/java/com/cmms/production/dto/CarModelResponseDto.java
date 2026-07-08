package com.cmms.production.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CarModelResponseDto {
    private Long id;
    private String modelName;
    private String fuelType;
    private String transmission;
    private BigDecimal basePrice;
    private List<String> colorOptions;
    private LocalDate launchDate;
    private boolean isActive;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime lastModifiedAt;
    private Long lastModifiedBy;


}
