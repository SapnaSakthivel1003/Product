package com.CMMS.Production.Dto;

import com.CMMS.Production.Entity.Result;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QualityInspectionResponseDto {

    private String inspectionNumber;
    private Long productionOrderId;
    private Long inspectorId;
    private Result inspectionResult;
    private String remarks;
    private LocalDateTime inspectedAt;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime lastModifiedAt;
    private Long lastModifiedBy;
}
