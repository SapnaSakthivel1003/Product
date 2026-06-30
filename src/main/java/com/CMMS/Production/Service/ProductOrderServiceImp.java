package com.CMMS.Production.Service;

import com.CMMS.Human.Resources.UserContext.UserContext;
import com.CMMS.Human.Resources.UserContext.UserContextHolder;
import com.CMMS.Master.Data.Entity.CarModel;
import com.CMMS.Master.Data.Entity.Plants;
import com.CMMS.Master.Data.Repository.CarModelRepository;
import com.CMMS.Master.Data.Repository.PlantsRepository;
import com.CMMS.Production.Dto.ProductRequestDto;
import com.CMMS.Production.Dto.ProductResponseDto;
import com.CMMS.Production.Entity.OrderStatus;
import com.CMMS.Production.Entity.ProductionOrder;
import com.CMMS.Production.Repository.ProductRepository;
import com.CMMS.Production.Utils.ProductionOrderMapper;
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
public class ProductOrderServiceImp implements ProductOrderService {
    private final ProductRepository productRepository;
    private final PlantsRepository plantRepository;
    private final CarModelRepository carModelRepository;
    private final ProductionOrderMapper productionOrderMapper;
    @Override
    public ProductResponseDto saveProduct(ProductRequestDto requestDto) {
        if (requestDto == null || requestDto.getPlantId() == null || requestDto.getCarModelId()==null) {
            throw new IllegalArgumentException("ID's must not be null in the request data.");
        }

        validatePlantExistence(requestDto.getPlantId());
        validateCarModelExistence(requestDto.getCarModelId());

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

        return productionOrderMapper.mapToResponseDto(savedProduct);
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        ProductionOrder existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));

        Plants plant = plantRepository.findById(requestDto.getPlantId())
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;

        CarModel carModer=carModelRepository.findById(requestDto.getCarModelId()).orElseThrow(() -> new RuntimeException("CarModel not found"));
        existingProduct.setOrderNumber(requestDto.getOrderNumber());
        existingProduct.setPlantId(plant);
        existingProduct.setCarModelId(carModer);
        existingProduct.setStatus(OrderStatus.valueOf(requestDto.getStatus()));
        existingProduct.setTargetQuantity(requestDto.getTargetQuantity());
        existingProduct.setCompletedQuantity(requestDto.getCompletedQuantity());
        existingProduct.setExpectedEndDate(requestDto.getExpectedEndDate());
        existingProduct.setActualEndDate(requestDto.getActualEndDate());
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

    private void validatePlantExistence(Long plantId) {
        if (!plantRepository.existsById(plantId)) {
            throw new IllegalArgumentException("Foreign key violation: Plant ID " + plantId + " does not exist.");
        }
    }
    private void validateCarModelExistence(Long carModelId) {
        if (!carModelRepository.existsById(carModelId)) {
            throw new IllegalArgumentException("Foreign key violation: Plant ID " + carModelId + " does not exist.");
        }
    }

}
