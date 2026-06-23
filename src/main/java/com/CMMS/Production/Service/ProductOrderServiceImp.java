package com.CMMS.Production.Service;

import com.CMMS.Master.Data.Entity.CarModel;
import com.CMMS.Master.Data.Entity.Plants;
import com.CMMS.Master.Data.Repository.CarModelRepository;
import com.CMMS.Master.Data.Repository.PlantsRepository;
import com.CMMS.Production.Dto.ProductRequestDto;
import com.CMMS.Production.Dto.ProductResponseDto;
import com.CMMS.Production.Entity.OrderStatus;
import com.CMMS.Production.Entity.ProductionOrder;
import com.CMMS.Production.Repository.ProductRepository;
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
    private final PlantsRepository plantRepository; // Injecting PlantRepository for FK verification
    private final CarModelRepository carModelRepository;
    @Override
    public ProductResponseDto saveProduct(ProductRequestDto requestDto) {
        if (requestDto == null || requestDto.getPlantId() == null || requestDto.getCarModelId()==null) {
            throw new IllegalArgumentException("ID's must not be null in the request data.");
        }

        validatePlantExistence(requestDto.getPlantId());
        validateCarModelExistence(requestDto.getCarModelId());

        ProductionOrder product = mapToEntity(requestDto);
        String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String generatedCode = "PO-" + dateString + "-" + randomSuffix;
        product.setOrderNumber(generatedCode);

        ProductionOrder savedProduct = productRepository.save(product);
        savedProduct.setCreatedAt(LocalDateTime.now());
        savedProduct.setLastModifiedAt(LocalDateTime.now());
        return mapToResponseDto(savedProduct);
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        ProductionOrder existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));

        Plants plant = plantRepository.findById(requestDto.getPlantId())
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        CarModel carModer=carModelRepository.findById(requestDto.getCarModelId()).orElseThrow(() -> new RuntimeException("CarModel not found"));
        existingProduct.setOrderNumber(requestDto.getOrderNumber());
        existingProduct.setPlantId(plant);
        existingProduct.setCarModelId(carModer);
        existingProduct.setStatus(OrderStatus.valueOf(requestDto.getStatus()));
        existingProduct.setTargetQuantity(requestDto.getTargetQuantity());
        existingProduct.setCompletedQuantity(requestDto.getCompletedQuantity());
        existingProduct.setExpectedEndDate(requestDto.getExpectedEndDate());
        existingProduct.setActualEndDate(requestDto.getActualEndDate());

        ProductionOrder updatedProduct = productRepository.save(existingProduct);
        updatedProduct.setLastModifiedAt(LocalDateTime.now());
        return mapToResponseDto(existingProduct);
    }

    @Override

    public ProductResponseDto getById(Long id) {
        ProductionOrder product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("product not found with ID: " + id));
        return mapToResponseDto(product);
    }

    @Override

    public List<ProductResponseDto> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
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
    // --- Mapper Logic ---
    private ProductionOrder mapToEntity(ProductRequestDto dto) {
        Plants plant = plantRepository.findById(dto.getPlantId())
                .orElseThrow(() -> new RuntimeException("Plant not found"));
        CarModel carModer=carModelRepository.findById(dto.getCarModelId()).orElseThrow(() -> new RuntimeException("Plant not found"));
        ProductionOrder product = new ProductionOrder();
        product.setOrderNumber(dto.getOrderNumber());
        product.setPlantId(plant);
        product.setCarModelId(carModer);
        product.setStatus(OrderStatus.valueOf(dto.getStatus()));
        product.setTargetQuantity(dto.getTargetQuantity());
        product.setCompletedQuantity(dto.getCompletedQuantity());
        product.setExpectedEndDate(dto.getExpectedEndDate());
        product.setActualEndDate(dto.getActualEndDate());
        product.setCreatedAt(dto.getCreatedAt());
        product.setCreatedBy(dto.getCreatedBy());
        product.setLastModifiedAt(dto.getLastModifiedAt());
        product.setLastModifiedBy(dto.getLastModifiedBy());

        return product;
    }

    private ProductResponseDto mapToResponseDto(ProductionOrder dto) {
        Plants plant = plantRepository.findById(dto.getPlantId().getId())
                .orElseThrow(() -> new RuntimeException("Plant not found"));
        CarModel carModer=carModelRepository.findById(dto.getCarModelId().getId()).orElseThrow(() -> new RuntimeException("Plant not found"));

        ProductResponseDto product = new ProductResponseDto();
        product.setOrderNumber(dto.getOrderNumber());
        product.setPlantId(plant.getId());
        product.setCarModelId(carModer.getId());
        product.setStatus(dto.getStatus().name());
        product.setTargetQuantity(dto.getTargetQuantity());
        product.setCompletedQuantity(dto.getCompletedQuantity());
        product.setExpectedEndDate(dto.getExpectedEndDate());
        product.setActualEndDate(dto.getActualEndDate());
        product.setCreatedAt(dto.getCreatedAt());
        product.setCreatedBy(dto.getCreatedBy());
        product.setLastModifiedAt(dto.getLastModifiedAt());
        product.setLastModifiedBy(dto.getLastModifiedBy());
        return product;
    }
}
