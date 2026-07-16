package com.cmms.production.service;

import com.cmms.production.dto.CarModelResponseDto;
import com.cmms.production.dto.DataChangeEventDto;
import com.cmms.production.dto.VehicleInventoryRequestDto;
import com.cmms.production.dto.VehicleInventoryResponseDto;
import com.cmms.production.entity.Status;
import com.cmms.production.entity.VehicleInventory;
import com.cmms.production.feignclients.AuditLogFeignClient;
import com.cmms.production.feignclients.Production;
import com.cmms.production.repository.ProductRepository;
import com.cmms.production.repository.VehicleInventoryRepository;
import com.cmms.production.user_context.UserContext;
import com.cmms.production.user_context.UserContextHolder;
import com.cmms.production.utils.VehicleInventoryMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleInventoryServiceImp implements VehicleInventoryService {

    private final ProductRepository productRepository;
    private final VehicleInventoryRepository vehicleInventoryRepository;
    private final VehicleInventoryMapper mapper;
    private final Production productionClient;
    private final AuditLogFeignClient feignClient;
    private final ObjectMapper objectMapper;
    @Override
    public VehicleInventoryResponseDto saveVehicleInventory(VehicleInventoryRequestDto requestDto) {
        if (requestDto == null || requestDto.getProductionOrder() == null || requestDto.getCarModel()==null) {
            throw new IllegalArgumentException("ID's must not be null in the request data.");
        }

        if (!productRepository.existsById(requestDto.getProductionOrder())) {
            throw new IllegalArgumentException("Foreign key violation: ProductionOrder ID  does not exist.");
        }
        CarModelResponseDto carModelResponseDto = productionClient.getCarModelById(requestDto.getCarModel()).getBody().getData();

        if (carModelResponseDto == null || carModelResponseDto.getColorOptions() == null || carModelResponseDto.getColorOptions().isEmpty()) {
            throw new RuntimeException("Car model or color options not found");
        }
        String rawColorString = carModelResponseDto.getColorOptions().get(0);
        String cleanColors = rawColorString.replace("[", "")
                .replace("]", "")
                .replace("\"", "");
        List<String> validColors = Arrays.stream(cleanColors.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        String requestedColor = requestDto.getColor() != null ? requestDto.getColor().trim() : "";
        boolean isValidColor = validColors.stream()
                .anyMatch(color -> color.equalsIgnoreCase(requestedColor));

        if (!isValidColor) {
            throw new RuntimeException("choose color from the car model");
        }
        if(!carModelResponseDto.isActive()){
            throw new RuntimeException("CarModel must be Active ");
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

        try {
            sendSyncRequest(savedvVehicleInventory, "CREATE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mapper.mapToResponseDto(savedvVehicleInventory);
    }

    @Override
    public VehicleInventoryResponseDto updateVehicleInventory(Long id, VehicleInventoryRequestDto requestDto) {
        VehicleInventory existingVehicleInventory = vehicleInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VehicleInventory not found with ID: " + id));

        if (!productRepository.existsById(requestDto.getProductionOrder())) {
            throw new IllegalArgumentException("Foreign key violation: ProductionOrder ID  does not exist.");
        }
        CarModelResponseDto carModelResponseDto= productionClient.getCarModelById(requestDto.getCarModel()).getBody().getData();
        assert carModelResponseDto != null;
        if(!carModelResponseDto.isActive()){
            throw new RuntimeException("CarModel must be Active ");
        }

        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;
        existingVehicleInventory.setCreatedAt(LocalDateTime.now());

        existingVehicleInventory.setLastModifiedAt(LocalDateTime.now());
        existingVehicleInventory.setLastModifiedBy(userId);

        VehicleInventory vehicleInventory = vehicleInventoryRepository.save(existingVehicleInventory);
        try {
            sendSyncRequest(vehicleInventory, "UPDATE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        VehicleInventory vehicleInventory = vehicleInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot delete. Production Order not found with ID: " + id));
        try {
            sendSyncRequest(vehicleInventory, "DELETE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        vehicleInventoryRepository.deleteById(id);
    }
    public boolean existsById(Long id) {
        return vehicleInventoryRepository.existsById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        log.info(">>> [PRODUCTION SERVICE] Updating vehicle inventory status. ID: {}, New Status: {}", id, status);

        VehicleInventory vehicle = vehicleInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle inventory record not found with ID: " + id));
         String statusDelivery = "DELIVERED";
         if (Objects.equals(vehicle.getStatus(), statusDelivery)) {
            throw new IllegalStateException("Cannot update status. This vehicle has already been delivered.");
        }
        vehicle.setStatus(Status.valueOf(status.toUpperCase()));
        vehicleInventoryRepository.save(vehicle);
        log.info(">>> [PRODUCTION SERVICE] Successfully updated vehicle status to {} in database.", status);
    }


    private void sendSyncRequest(VehicleInventory vehicleInventory, String actionStatus) throws Exception {
        String jsonPayload = objectMapper.writeValueAsString(vehicleInventory);
        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;

        DataChangeEventDto dto = new DataChangeEventDto();
        dto.setAction(actionStatus);
        dto.setTableName("VehicleInventory");
        dto.setChangedData(jsonPayload);
        dto.setPerformedBy(userId);
        dto.setRecordId(vehicleInventory.getId());
        dto.setIpAddress(getSystemIpAddress());
        dto.setCreatedBy(userId);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setLastModifiedBy(userId);
        dto.setLastModifiedAt(LocalDateTime.now());
        feignClient.createAuditLogs(dto);
    }

    public String getSystemIpAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }


}
