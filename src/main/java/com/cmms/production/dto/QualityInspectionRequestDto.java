package com.cmms.production.dto;

import com.cmms.production.entity.Result;
import lombok.Data;

import java.time.LocalDate;


@Data
public class QualityInspectionRequestDto {
    private String inspectionNumber;
    private Long productionOrderId;
    private Long inspectorId;
    private Result inspectionResult;
    private String remarks;
    private LocalDate inspectedAt;
}
