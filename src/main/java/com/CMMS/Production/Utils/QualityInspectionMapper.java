package com.CMMS.Production.Utils;

import com.CMMS.Master.Data.Entity.Employee;
import com.CMMS.Master.Data.Repository.EmployeeRepository;
import com.CMMS.Production.Dto.QualityInspectionRequestDto;
import com.CMMS.Production.Dto.QualityInspectionResponseDto;
import com.CMMS.Production.Entity.ProductionOrder;
import com.CMMS.Production.Entity.QualityInspection;
import com.CMMS.Production.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QualityInspectionMapper {
    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;
    public QualityInspection mapToEntity(QualityInspectionRequestDto dto) {
        Employee employee = employeeRepository.findById(dto.getInspectorId())
                .orElseThrow(() -> new RuntimeException("employee not found"));
        ProductionOrder productionOrder=productRepository.findById(dto.getProductionOrderId()).orElseThrow(() -> new RuntimeException("Plant not found"));

        QualityInspection qualityInspection = new QualityInspection();
        qualityInspection.setInspectionNumber(dto.getInspectionNumber());
        qualityInspection.setProductionOrderId(productionOrder);
        qualityInspection.setInspectorId(employee);
        qualityInspection.setInspectionResult(dto.getInspectionResult());
        qualityInspection.setRemarks(dto.getRemarks());
        qualityInspection.setInspectedAt(dto.getInspectedAt());
        qualityInspection.setCreatedAt(dto.getCreatedAt());
        qualityInspection.setCreatedBy(dto.getCreatedBy());
        qualityInspection.setLastModifiedAt(dto.getLastModifiedAt());
        qualityInspection.setLastModifiedBy(dto.getLastModifiedBy());

        return qualityInspection;
    }

    public QualityInspectionResponseDto mapToResponseDto(QualityInspection dto) {
        Employee employee = employeeRepository.findById(dto.getInspectorId().getId())
                .orElseThrow(() -> new RuntimeException("employee not found"));
        ProductionOrder productionOrder=productRepository.findById(dto.getProductionOrderId().getId()).orElseThrow(() -> new RuntimeException("Plant not found"));

        QualityInspectionResponseDto qualityInspection = new QualityInspectionResponseDto();
        qualityInspection.setInspectionNumber(dto.getInspectionNumber());
        qualityInspection.setProductionOrderId(productionOrder.getId());
        qualityInspection.setInspectorId(employee.getId());
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
