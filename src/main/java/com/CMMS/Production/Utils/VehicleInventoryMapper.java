package com.CMMS.Production.Utils;

import com.CMMS.Master.Data.Entity.CarModel;
import com.CMMS.Master.Data.Repository.CarModelRepository;
import com.CMMS.Production.Dto.VehicleInventoryRequestDto;
import com.CMMS.Production.Dto.VehicleInventoryResponseDto;
import com.CMMS.Production.Entity.ProductionOrder;
import com.CMMS.Production.Entity.VehicleInventory;
import com.CMMS.Production.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VehicleInventoryMapper {
    private final ProductRepository productRepository;
    private final CarModelRepository carModelRepository;
    public VehicleInventory mapToEntity(VehicleInventoryRequestDto dto) {
        ProductionOrder productionOrder = productRepository.findById(dto.getProductionOrder().getId())
                .orElseThrow(() -> new RuntimeException("productionOrder not found"));
        CarModel carModel=carModelRepository.findById(dto.getCarModel().getId()).orElseThrow(() -> new RuntimeException("carModel not found"));

        VehicleInventory vehicleInventory = new VehicleInventory();
        vehicleInventory.setVin(vehicleInventory.getVin());
        vehicleInventory.setProductionOrder(productionOrder);
        vehicleInventory.setCarModel(carModel);
        vehicleInventory.setColor(vehicleInventory.getColor());
        vehicleInventory.setStatus(vehicleInventory.getStatus());
        vehicleInventory.setManufacturedDate(vehicleInventory.getManufacturedDate());
        vehicleInventory.setCreatedAt(LocalDateTime.now());
        vehicleInventory.setCreatedBy(dto.getCreatedBy());
        vehicleInventory.setLastModifiedAt(LocalDateTime.now());
        vehicleInventory.setLastModifiedBy(dto.getLastModifiedBy());
        return vehicleInventory;
    }

    public VehicleInventoryResponseDto mapToResponseDto(VehicleInventory dto) {
        ProductionOrder productionOrder = productRepository.findById(dto.getProductionOrder().getId())
                .orElseThrow(() -> new RuntimeException("productionOrder not found"));
        CarModel carModel=carModelRepository.findById(dto.getCarModel().getId()).orElseThrow(() -> new RuntimeException("carModel not found"));
        VehicleInventoryResponseDto vehicleInventory = new VehicleInventoryResponseDto();
        vehicleInventory.setVin(vehicleInventory.getVin());
        vehicleInventory.setProductionOrder(productionOrder);
        vehicleInventory.setCarModel(carModel);
        vehicleInventory.setColor(vehicleInventory.getColor());
        vehicleInventory.setStatus(vehicleInventory.getStatus());
        vehicleInventory.setManufacturedDate(vehicleInventory.getManufacturedDate());
        vehicleInventory.setCreatedAt(LocalDateTime.now());
        vehicleInventory.setCreatedBy(dto.getCreatedBy());
        vehicleInventory.setLastModifiedAt(LocalDateTime.now());
        vehicleInventory.setLastModifiedBy(dto.getLastModifiedBy());
        return vehicleInventory;
    }
}
