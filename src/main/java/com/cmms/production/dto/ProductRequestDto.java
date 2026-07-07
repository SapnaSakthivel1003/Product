package com.cmms.production.dto;

import lombok.Data;

import java.time.LocalDate;


@Data
public class ProductRequestDto {
    private String orderNumber;
    private Long plantId;
    private Long carModelId;
    private String status;
    private Integer targetQuantity;
    private Integer completedQuantity;
    private LocalDate expectedEndDate;
    private LocalDate actualEndDate;
}
