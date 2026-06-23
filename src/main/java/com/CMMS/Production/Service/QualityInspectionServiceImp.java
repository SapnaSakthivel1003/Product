package com.CMMS.Production.Service;

import com.CMMS.Master.Data.Entity.Employee;
import com.CMMS.Master.Data.Repository.EmployeeRepository;
import com.CMMS.Production.Dto.QualityInspectionRequestDto;
import com.CMMS.Production.Dto.QualityInspectionResponseDto;
import com.CMMS.Production.Entity.ProductionOrder;
import com.CMMS.Production.Entity.QualityInspection;
import com.CMMS.Production.Repository.ProductRepository;
import com.CMMS.Production.Repository.QualityInspectionRepository;
import com.CMMS.Production.Utils.QualityInspectionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QualityInspectionServiceImp implements QualityInspectionService{
    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository; // Injecting PlantRepository for FK verification
    private final QualityInspectionRepository qualityInspectionRepository;
    private final QualityInspectionMapper mapper;
    @Override
    public QualityInspectionResponseDto saveQualityInspection(QualityInspectionRequestDto requestDto) {
        if (requestDto == null || requestDto.getInspectorId() == null || requestDto.getProductionOrderId()==null) {
            throw new IllegalArgumentException("ID's must not be null in the request data.");
        }

        validateProductExistence(requestDto.getProductionOrderId());
        validateEmployeeExistence(requestDto.getInspectorId());

        QualityInspection qualityInspection = mapper.mapToEntity(requestDto);
        String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String generatedCode = "QI-" + dateString + "-" + randomSuffix;
        qualityInspection.setInspectionNumber(generatedCode);

        qualityInspection.setCreatedAt(LocalDateTime.now());
        qualityInspection.setLastModifiedAt(LocalDateTime.now());
        QualityInspection savedQualityInspection = qualityInspectionRepository.save(qualityInspection);

        return mapper.mapToResponseDto(savedQualityInspection);
    }

    @Override
    public QualityInspectionResponseDto updateQualityInspection(Long id, QualityInspectionRequestDto requestDto) {
        QualityInspection existingQualityInspection = qualityInspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));
        Employee employee = employeeRepository.findById(requestDto.getInspectorId())
                .orElseThrow(() -> new RuntimeException("employee not found"));
        ProductionOrder productionOrder=productRepository.findById(requestDto.getProductionOrderId()).orElseThrow(() -> new RuntimeException("Plant not found"));

        existingQualityInspection.setInspectionNumber(requestDto.getInspectionNumber());
        existingQualityInspection.setProductionOrderId(productionOrder);
        existingQualityInspection.setInspectorId(employee);
        existingQualityInspection.setInspectionResult(requestDto.getInspectionResult());
        existingQualityInspection.setRemarks(requestDto.getRemarks());
        existingQualityInspection.setInspectedAt(requestDto.getInspectedAt());
        existingQualityInspection.setCreatedAt(LocalDateTime.now());

        existingQualityInspection.setLastModifiedAt(LocalDateTime.now());
        existingQualityInspection.setCreatedBy(requestDto.getCreatedBy());
        existingQualityInspection.setLastModifiedBy(requestDto.getLastModifiedBy());

        QualityInspection qualityInspection = qualityInspectionRepository.save(existingQualityInspection);
        return mapper.mapToResponseDto(qualityInspection);
    }

    @Override

    public QualityInspectionResponseDto getById(Long id) {
        QualityInspection qualityInspection = qualityInspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("product not found with ID: " + id));
        return mapper.mapToResponseDto(qualityInspection);
    }

    @Override

    public List<QualityInspectionResponseDto> getAll() {
        return qualityInspectionRepository.findAll()
                .stream()
                .map(mapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        if (!qualityInspectionRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Employee not found with ID: " + id);
        }
        qualityInspectionRepository.deleteById(id);
    }

    private void validateProductExistence(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Foreign key violation: Plant ID " + productId + " does not exist.");
        }
    }
    private void validateEmployeeExistence(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("Foreign key violation: Plant ID " + employeeId + " does not exist.");
        }
    }
    // --- Mapper Logic ---
//    private QualityInspection mapToEntity(QualityInspectionRequestDto dto) {
//        Employee employee = employeeRepository.findById(dto.getInspectorId())
//                .orElseThrow(() -> new RuntimeException("employee not found"));
//        ProductionOrder productionOrder=productRepository.findById(dto.getProductionOrderId()).orElseThrow(() -> new RuntimeException("Plant not found"));
//
//        QualityInspection qualityInspection = new QualityInspection();
//        qualityInspection.setInspectionNumber(dto.getInspectionNumber());
//        qualityInspection.setProductionOrderId(productionOrder);
//        qualityInspection.setInspectorId(employee);
//        qualityInspection.setInspectionResult(dto.getInspectionResult());
//        qualityInspection.setRemarks(dto.getRemarks());
//        qualityInspection.setInspectedAt(dto.getInspectedAt());
//        qualityInspection.setCreatedAt(dto.getCreatedAt());
//        qualityInspection.setCreatedBy(dto.getCreatedBy());
//        qualityInspection.setLastModifiedAt(dto.getLastModifiedAt());
//        qualityInspection.setLastModifiedBy(dto.getLastModifiedBy());
//
//        return qualityInspection;
//    }
//
//    private QualityInspectionResponseDto mapToResponseDto(QualityInspection dto) {
//        Employee employee = employeeRepository.findById(dto.getInspectorId().getId())
//                .orElseThrow(() -> new RuntimeException("employee not found"));
//        ProductionOrder productionOrder=productRepository.findById(dto.getProductionOrderId().getId()).orElseThrow(() -> new RuntimeException("Plant not found"));
//
//        QualityInspectionResponseDto qualityInspection = new QualityInspectionResponseDto();
//        qualityInspection.setInspectionNumber(dto.getInspectionNumber());
//        qualityInspection.setProductionOrderId(productionOrder.getId());
//        qualityInspection.setInspectorId(employee.getId());
//        qualityInspection.setInspectionResult(dto.getInspectionResult());
//        qualityInspection.setRemarks(dto.getRemarks());
//        qualityInspection.setInspectedAt(dto.getInspectedAt());
//        qualityInspection.setCreatedAt(dto.getCreatedAt());
//        qualityInspection.setCreatedBy(dto.getCreatedBy());
//        qualityInspection.setLastModifiedAt(dto.getLastModifiedAt());
//        qualityInspection.setLastModifiedBy(dto.getLastModifiedBy());
//        return qualityInspection;
//    }
}
