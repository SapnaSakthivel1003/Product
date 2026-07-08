package com.cmms.production.utils;

import com.cmms.production.dto.ProductRequestDto;
import com.cmms.production.dto.ProductResponseDto;
import com.cmms.production.entity.OrderStatus;
import com.cmms.production.entity.ProductionOrder;
import com.cmms.production.entity.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductionOrderMapper {

    public ProductionOrder mapToEntity(ProductRequestDto dto) {
        ProductionOrder product = new ProductionOrder();
        product.setOrderNumber(dto.getOrderNumber());
        product.setPlantId(dto.getPlantId());
        product.setCarModelId(dto.getCarModelId());
        product.setStatus(OrderStatus.valueOf(dto.getStatus()));
        product.setTargetQuantity(dto.getTargetQuantity());
        product.setCompletedQuantity(dto.getCompletedQuantity());
        product.setExpectedEndDate(dto.getExpectedEndDate());
        product.setActualEndDate(dto.getActualEndDate());
        return product;
    }

    public ProductResponseDto mapToResponseDto(ProductionOrder dto) {
        ProductResponseDto product = new ProductResponseDto();
        product.setId(dto.getId());
        product.setOrderNumber(dto.getOrderNumber());
        product.setPlantId(dto.getPlantId());
        product.setCarModelId(dto.getCarModelId());
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
