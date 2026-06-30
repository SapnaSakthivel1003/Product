package com.CMMS.Production.Utils;

import com.CMMS.Master.Data.Entity.CarModel;
import com.CMMS.Master.Data.Entity.Plants;
import com.CMMS.Master.Data.Repository.CarModelRepository;
import com.CMMS.Master.Data.Repository.PlantsRepository;
import com.CMMS.Production.Dto.ProductRequestDto;
import com.CMMS.Production.Dto.ProductResponseDto;
import com.CMMS.Production.Entity.OrderStatus;
import com.CMMS.Production.Entity.ProductionOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductionOrderMapper {
    private final PlantsRepository plantRepository;
    private final CarModelRepository carModelRepository;
    public ProductionOrder mapToEntity(ProductRequestDto dto) {
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
        return product;
    }

    public ProductResponseDto mapToResponseDto(ProductionOrder dto) {
        Plants plant = plantRepository.findById(dto.getPlantId().getId())
                .orElseThrow(() -> new RuntimeException("Plant not found"));
        CarModel carModer=carModelRepository.findById(dto.getCarModelId().getId()).orElseThrow(() -> new RuntimeException("Plant not found"));

        ProductResponseDto product = new ProductResponseDto();
        product.setId(dto.getId());
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
