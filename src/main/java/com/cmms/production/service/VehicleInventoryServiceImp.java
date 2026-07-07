package com.cmms.production.service;


import com.cmms.production.dto.VehicleInventoryRequestDto;
import com.cmms.production.dto.VehicleInventoryResponseDto;
import com.cmms.production.entity.VehicleInventory;
import com.cmms.production.feignClients.Production;
import com.cmms.production.repository.ProductRepository;
import com.cmms.production.repository.VehicleInventoryRepository;
import com.cmms.production.user_context.UserContext;
import com.cmms.production.user_context.UserContextHolder;
import com.cmms.production.utils.VehicleInventoryMapper;
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
    private final VehicleInventoryRepository vehicleInventoryRepository;
    private final VehicleInventoryMapper mapper;
    private final Production productionClient;
    @Override
    public VehicleInventoryResponseDto saveVehicleInventory(VehicleInventoryRequestDto requestDto) {
        if (requestDto == null || requestDto.getProductionOrder() == null || requestDto.getCarModel()==null) {
            throw new IllegalArgumentException("ID's must not be null in the request data.");
        }
        Boolean carMordelExists = productionClient.existsCarModelById(requestDto.getCarModel());

        if (Boolean.FALSE.equals(carMordelExists)) {
            throw new IllegalArgumentException("CarModel ID " + requestDto.getCarModel() + " does not exist in master data.");
        }
        if (!productRepository.existsById(requestDto.getProductionOrder())) {
            throw new IllegalArgumentException("Foreign key violation: ProductionOrder ID  does not exist.");
        }
        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;
        VehicleInventory vehicleInventory = mapper.mapToEntity(requestDto);
        String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0,4).toUpperCase();
        String generatedCode = "WMI-" + dateString + "-" + randomSuffix;
        vehicleInventory.setVin(generatedCode);

        vehicleInventory.setCreatedAt(LocalDateTime.now());
        vehicleInventory.setLastModifiedAt(LocalDateTime.now());
        vehicleInventory.setLastModifiedBy(userId);
        vehicleInventory.setCreatedBy(userId);
        VehicleInventory savedvVehicleInventory = vehicleInventoryRepository.save(vehicleInventory);

        return mapper.mapToResponseDto(savedvVehicleInventory);
    }

    @Override
    public VehicleInventoryResponseDto updateVehicleInventory(Long id, VehicleInventoryRequestDto requestDto) {
        VehicleInventory existingVehicleInventory = vehicleInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VehicleInventory not found with ID: " + id));

        Boolean carMordelExists = productionClient.existsCarModelById(requestDto.getCarModel());

        if (Boolean.FALSE.equals(carMordelExists)) {
            throw new IllegalArgumentException("CarModel ID " + requestDto.getCarModel() + " does not exist in master data.");
        }
        if (!productRepository.existsById(requestDto.getProductionOrder())) {
            throw new IllegalArgumentException("Foreign key violation: ProductionOrder ID  does not exist.");
        }
        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;
        existingVehicleInventory.setCreatedAt(LocalDateTime.now());

        existingVehicleInventory.setLastModifiedAt(LocalDateTime.now());
        existingVehicleInventory.setLastModifiedBy(userId);

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




}
