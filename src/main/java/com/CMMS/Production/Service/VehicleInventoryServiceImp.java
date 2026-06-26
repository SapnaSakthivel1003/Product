package com.CMMS.Production.Service;

import com.CMMS.Master.Data.Entity.CarModel;
import com.CMMS.Master.Data.Repository.CarModelRepository;
import com.CMMS.Production.Dto.VehicleInventoryRequestDto;
import com.CMMS.Production.Dto.VehicleInventoryResponseDto;
import com.CMMS.Production.Entity.ProductionOrder;
import com.CMMS.Production.Entity.VehicleInventory;
import com.CMMS.Production.Repository.ProductRepository;
import com.CMMS.Production.Repository.VehicleInventoryRepository;
import com.CMMS.Production.Utils.VehicleInventoryMapper;
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
public class VehicleInventoryServiceImp implements VehicleInventoryService {

    private final ProductRepository productRepository;
    private final CarModelRepository carModelRepository;
    private final VehicleInventoryRepository vehicleInventoryRepository;
    private final VehicleInventoryMapper mapper;
    @Override
    public VehicleInventoryResponseDto saveVehicleInventory(VehicleInventoryRequestDto requestDto) {
        if (requestDto == null || requestDto.getProductionOrder() == null || requestDto.getCarModel()==null) {
            throw new IllegalArgumentException("ID's must not be null in the request data.");
        }

        validateProductExistence(requestDto.getProductionOrder().getId());
        validateCarModelExistence(requestDto.getCarModel().getId());

        VehicleInventory vehicleInventory = mapper.mapToEntity(requestDto);
        String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String generatedCode = "WMI-" + dateString + "-" + randomSuffix;
        vehicleInventory.setVin(generatedCode);

        vehicleInventory.setCreatedAt(LocalDateTime.now());
        vehicleInventory.setLastModifiedAt(LocalDateTime.now());
        VehicleInventory savedvVehicleInventory = vehicleInventoryRepository.save(vehicleInventory);

        return mapper.mapToResponseDto(savedvVehicleInventory);
    }

    @Override
    public VehicleInventoryResponseDto updateVehicleInventory(Long id, VehicleInventoryRequestDto requestDto) {
        VehicleInventory existingVehicleInventory = vehicleInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VehicleInventory not found with ID: " + id));
        ProductionOrder productionOrder = productRepository.findById(requestDto.getProductionOrder().getId())
                .orElseThrow(() -> new RuntimeException("productionOrder not found"));
        CarModel carModel=carModelRepository.findById(requestDto.getCarModel().getId()).orElseThrow(() -> new RuntimeException("carModel not found"));

        existingVehicleInventory.setVin(requestDto.getVin());
        existingVehicleInventory.setProductionOrder(productionOrder);
        existingVehicleInventory.setCarModel(carModel);
        existingVehicleInventory.setColor(requestDto.getColor());
        existingVehicleInventory.setStatus(requestDto.getStatus());
        existingVehicleInventory.setManufacturedDate(requestDto.getManufacturedDate());
        existingVehicleInventory.setCreatedAt(LocalDateTime.now());
        existingVehicleInventory.setCreatedBy(requestDto.getCreatedBy());
        existingVehicleInventory.setLastModifiedAt(LocalDateTime.now());
        existingVehicleInventory.setLastModifiedBy(requestDto.getLastModifiedBy());

        VehicleInventory vehicleInventory = vehicleInventoryRepository.save(existingVehicleInventory);
        return mapper.mapToResponseDto(vehicleInventory);
    }

    @Override

    public VehicleInventoryResponseDto getById(Long id) {
        VehicleInventory vehicleInventory = vehicleInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("product not found with ID: " + id));
        return mapper.mapToResponseDto(vehicleInventory);
    }

    @Override

    public List<VehicleInventoryResponseDto> getAll() {
        return vehicleInventoryRepository.findAll()
                .stream()
                .map(mapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        if (!vehicleInventoryRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Employee not found with ID: " + id);
        }
        vehicleInventoryRepository.deleteById(id);
    }

    private void validateProductExistence(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Foreign key violation: Plant ID " + productId + " does not exist.");
        }
    }
    private void validateCarModelExistence(Long carModelId) {
        if (!carModelRepository.existsById(carModelId)) {
            throw new IllegalArgumentException("Foreign key violation: Plant ID " + carModelId + " does not exist.");
        }
    }

}
