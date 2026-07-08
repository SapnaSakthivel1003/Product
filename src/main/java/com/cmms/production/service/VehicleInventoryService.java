package com.cmms.production.service;

import com.cmms.production.dto.VehicleInventoryRequestDto;
import com.cmms.production.dto.VehicleInventoryResponseDto;

import java.util.List;

public interface VehicleInventoryService {
    VehicleInventoryResponseDto saveVehicleInventory(VehicleInventoryRequestDto requestDto);
    VehicleInventoryResponseDto getById(Long id);
    List<VehicleInventoryResponseDto> getAll();
    VehicleInventoryResponseDto updateVehicleInventory(Long id, VehicleInventoryRequestDto requestDto);
    void deleteById(Long id);
    void updateStatus(Long id, String status);

}
