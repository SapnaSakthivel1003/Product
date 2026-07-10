package com.cmms.production.utils;

import com.cmms.production.dto.QualityInspectionRequestDto;
import com.cmms.production.dto.QualityInspectionResponseDto;
import com.cmms.production.entity.QualityInspection;
import com.cmms.production.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QualityInspectionMapper {
  private final ProductRepository productRepository;

    public QualityInspection mapToEntity(QualityInspectionRequestDto dto) {
        QualityInspection qualityInspection = new QualityInspection();
        qualityInspection.setInspectionNumber(dto.getInspectionNumber());
        qualityInspection.setProductionOrderId(dto.getProductionOrderId());
        qualityInspection.setInspectorId(dto.getInspectorId());
        qualityInspection.setInspectionResult(dto.getInspectionResult());
        qualityInspection.setRemarks(dto.getRemarks());
        qualityInspection.setInspectedAt(dto.getInspectedAt());

        return qualityInspection;
    }

    public QualityInspectionResponseDto mapToResponseDto(QualityInspection dto) {
        QualityInspectionResponseDto qualityInspection = new QualityInspectionResponseDto();
        qualityInspection.setId(dto.getId());
        qualityInspection.setInspectionNumber(dto.getInspectionNumber());
        qualityInspection.setProductionOrderId(dto.getProductionOrderId());
        qualityInspection.setInspectorId(dto.getInspectorId());
        qualityInspection.setInspectionResult(dto.getInspectionResult());
        qualityInspection.setRemarks(dto.getRemarks());
        qualityInspection.setInspectedAt(dto.getInspectedAt());
        qualityInspection.setCreatedAt(dto.getCreatedAt());
        qualityInspection.setCreatedBy(dto.getCreatedBy());
        qualityInspection.setLastModifiedAt(dto.getLastModifiedAt());
        qualityInspection.setLastModifiedBy(dto.getLastModifiedBy());
        return qualityInspection;
    }
}
