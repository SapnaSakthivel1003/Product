package com.CMMS.Production.Service;

import com.CMMS.Production.Dto.VehicleInventoryRequestDto;
import com.CMMS.Production.Dto.VehicleInventoryResponseDto;

import java.util.List;

public interface VehicleInventoryService {
    VehicleInventoryResponseDto saveVehicleInventory(VehicleInventoryRequestDto requestDto);
    VehicleInventoryResponseDto getById(Long id);
    List<VehicleInventoryResponseDto> getAll();
    VehicleInventoryResponseDto updateVehicleInventory(Long id, VehicleInventoryRequestDto requestDto);
    void deleteById(Long id);
}
