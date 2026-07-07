package com.cmms.production.feignClients;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarModelResponseDto {
    private Long id;
    private String modelName;
    private String fuelType;
    private String transmission;
    private BigDecimal basePrice;
    private Boolean isActive;
}