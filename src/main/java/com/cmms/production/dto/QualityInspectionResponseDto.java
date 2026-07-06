package com.cmms.production.dto;

import com.cmms.production.entity.Result;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class QualityInspectionResponseDto {

    private Long id;
    private String inspectionNumber;
    private Long productionOrderId;
    private Long inspectorId;
    private Result inspectionResult;
    private String remarks;
    private LocalDate inspectedAt;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime lastModifiedAt;
    private Long lastModifiedBy;
}
