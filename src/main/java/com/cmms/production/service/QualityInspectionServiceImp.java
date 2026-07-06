package com.cmms.production.service;

import com.cmms.production.dto.QualityInspectionRequestDto;
import com.cmms.production.dto.QualityInspectionResponseDto;
import com.cmms.production.entity.QualityInspection;
import com.cmms.production.feignClients.Production;
import com.cmms.production.repository.ProductRepository;
import com.cmms.production.repository.QualityInspectionRepository;
import com.cmms.production.user_context.UserContext;
import com.cmms.production.user_context.UserContextHolder;
import com.cmms.production.utils.QualityInspectionMapper;
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
    private final QualityInspectionRepository qualityInspectionRepository;
    private final QualityInspectionMapper mapper;
    private final Production productionClient;
    @Override
    public QualityInspectionResponseDto saveQualityInspection(QualityInspectionRequestDto requestDto) {
        if (requestDto == null || requestDto.getInspectorId() == null || requestDto.getProductionOrderId()==null) {
            throw new IllegalArgumentException("ID's must not be null in the request data.");
        }
        Boolean employeeExists = productionClient.existsEmployeeById(requestDto.getInspectorId());

        if (Boolean.FALSE.equals(employeeExists)) {
            throw new IllegalArgumentException("Plant ID " + requestDto.getInspectorId() + " does not exist in master data.");
        }
        if (!productRepository.existsById(requestDto.getProductionOrderId())) {
            throw new IllegalArgumentException("Foreign key violation: Plant ID  does not exist.");
        }
        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;

        QualityInspection qualityInspection = mapper.mapToEntity(requestDto);
        String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String generatedCode = "QI-" + dateString + "-" + randomSuffix;
        qualityInspection.setInspectionNumber(generatedCode);
        qualityInspection.setCreatedAt(LocalDateTime.now());
        qualityInspection.setLastModifiedAt(LocalDateTime.now());
        qualityInspection.setCreatedBy(userId);
        qualityInspection.setLastModifiedBy(userId);
        QualityInspection savedQualityInspection = qualityInspectionRepository.save(qualityInspection);

        return mapper.mapToResponseDto(savedQualityInspection);
    }

    @Override
    public QualityInspectionResponseDto updateQualityInspection(Long id, QualityInspectionRequestDto requestDto) {
        QualityInspection existingQualityInspection = qualityInspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));

        Boolean employeeExists = productionClient.existsCarModelById(requestDto.getInspectorId());

        if (Boolean.FALSE.equals(employeeExists)) {
            throw new IllegalArgumentException("production ID " + requestDto.getInspectorId() + " does not exist in master data.");
        }
        if (!productRepository.existsById(requestDto.getProductionOrderId())) {
            throw new IllegalArgumentException("Foreign key violation: production ID  does not exist.");
        }
        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;
        existingQualityInspection.setLastModifiedAt(LocalDateTime.now());
        existingQualityInspection.setLastModifiedBy(userId);
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


}
