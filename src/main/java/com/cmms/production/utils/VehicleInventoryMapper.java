package com.cmms.production.utils;


import com.cmms.production.dto.VehicleInventoryRequestDto;
import com.cmms.production.dto.VehicleInventoryResponseDto;
import com.cmms.production.entity.VehicleInventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class VehicleInventoryMapper {
    public VehicleInventory mapToEntity(VehicleInventoryRequestDto dto) {

        VehicleInventory vehicleInventory = new VehicleInventory();
        vehicleInventory.setVin(dto.getVin());
        vehicleInventory.setProductionOrder(dto.getProductionOrder());
        vehicleInventory.setCarModel(dto.getCarModel());
        vehicleInventory.setColor(dto.getColor());
        vehicleInventory.setStatus(dto.getStatus());
        vehicleInventory.setManufacturedDate(dto.getManufacturedDate());
        return vehicleInventory;
    }

    public VehicleInventoryResponseDto mapToResponseDto(VehicleInventory dto) {

        VehicleInventoryResponseDto vehicleInventory = new VehicleInventoryResponseDto();
        vehicleInventory.setId(dto.getId());
        vehicleInventory.setVin(dto.getVin());
        vehicleInventory.setProductionOrder(dto.getProductionOrder());
        vehicleInventory.setCarModel(dto.getCarModel());
        vehicleInventory.setColor(dto.getColor());
        vehicleInventory.setStatus(dto.getStatus());
        vehicleInventory.setManufacturedDate(dto.getManufacturedDate());
        vehicleInventory.setCreatedAt(dto.getCreatedAt());
        vehicleInventory.setCreatedBy(dto.getCreatedBy());
        vehicleInventory.setLastModifiedAt(dto.getLastModifiedAt());
        vehicleInventory.setLastModifiedBy(dto.getLastModifiedBy());
        return vehicleInventory;
    }
}
