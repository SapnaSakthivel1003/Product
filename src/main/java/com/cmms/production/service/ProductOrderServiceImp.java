package com.cmms.production.service;

import com.cmms.production.dto.DataChangeEventDto;
import com.cmms.production.dto.ProductRequestDto;
import com.cmms.production.dto.ProductResponseDto;
import com.cmms.production.entity.ProductionOrder;
import com.cmms.production.feignClients.AuditLogFeignClient;
import com.cmms.production.feignClients.Production;
import com.cmms.production.repository.ProductRepository;
import com.cmms.production.user_context.UserContext;
import com.cmms.production.user_context.UserContextHolder;
import com.cmms.production.utils.ProductionOrderMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductOrderServiceImp implements ProductOrderService {
    private final ProductRepository productRepository;
    private final Production productionClient;
    private final ProductionOrderMapper productionOrderMapper;
    private final AuditLogFeignClient feignClient;
    private final ObjectMapper objectMapper;
    @Override
    @Transactional
    public ProductResponseDto saveProduct(ProductRequestDto requestDto) {
        if (requestDto == null || requestDto.getPlantId() == null || requestDto.getCarModelId() == null) {
            throw new IllegalArgumentException("IDs must not be null in the request data.");
        }

        Boolean plantExists = productionClient.existsPlantById(requestDto.getPlantId());
        Boolean carModelExists = productionClient.existsCarModelById(requestDto.getCarModelId());

        if (Boolean.FALSE.equals(plantExists)) {
            throw new IllegalArgumentException("Plant ID " + requestDto.getPlantId() + " does not exist in master data.");
        }
        if (Boolean.FALSE.equals(carModelExists)) {
            throw new IllegalArgumentException("Car Model ID " + requestDto.getCarModelId() + " does not exist in master data.");
        }

        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;

        ProductionOrder product = productionOrderMapper.mapToEntity(requestDto);
        String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String generatedCode = "PO-" + dateString + "-" + randomSuffix;
        product.setOrderNumber(generatedCode);
        product.setCreatedAt(LocalDateTime.now());
        product.setLastModifiedAt(LocalDateTime.now());
        product.setCreatedBy(userId);
        product.setLastModifiedBy(userId);
        ProductionOrder savedProduct = productRepository.save(product);
        try {
            sendSyncRequest(savedProduct, "CREATED");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return productionOrderMapper.mapToResponseDto(savedProduct);
    }


    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        ProductionOrder existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));

        Boolean plantExists = productionClient.existsPlantById(requestDto.getPlantId());
        Boolean carModelExists = productionClient.existsCarModelById(requestDto.getCarModelId());

        if (Boolean.FALSE.equals(plantExists)) {
            throw new IllegalArgumentException("Plant ID " + requestDto.getPlantId() + " does not exist in master data.");
        }
        if (Boolean.FALSE.equals(carModelExists)) {
            throw new IllegalArgumentException("Car Model ID " + requestDto.getCarModelId() + " does not exist in master data.");
        }


        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;
        existingProduct.setLastModifiedAt(LocalDateTime.now());
        existingProduct.setLastModifiedBy(userId);
        ProductionOrder updatedProduct = productRepository.save(existingProduct);

        return productionOrderMapper.mapToResponseDto(updatedProduct);
    }

    @Override

    public ProductResponseDto getById(Long id) {
        ProductionOrder product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("product not found with ID: " + id));
        return productionOrderMapper.mapToResponseDto(product);
    }

    @Override

    public List<ProductResponseDto> getAll() {
        return productRepository.findAll()
                .stream()
                .map(productionOrderMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Employee not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }

    private void sendSyncRequest(ProductionOrder productionOrder, String actionStatus) throws Exception {
        String jsonPayload = objectMapper.writeValueAsString(productionOrder);

        DataChangeEventDto dto = new DataChangeEventDto();
        dto.setStatus(actionStatus);
        dto.setEntityName("ProductionOder");
        dto.setChangedData(jsonPayload);

        feignClient.sendDataChangeToRemoteServer(dto);
    }

}
