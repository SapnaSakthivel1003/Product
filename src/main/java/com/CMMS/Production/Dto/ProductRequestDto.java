package com.CMMS.Production.Dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductRequestDto {
    private Long id;
    private String orderNumber;
    private Long plantId;
    private Long carModelId;
    private String status;
    private Integer targetQuantity;
    private Integer completedQuantity;
    private LocalDate expectedEndDate;
    private LocalDate actualEndDate;
}
