package com.cmms.production.service;

import com.cmms.production.dto.*;
import com.cmms.production.entity.OrderStatus;
import com.cmms.production.entity.ProductionOrder;
import com.cmms.production.feignClients.AuditLogFeignClient;
import com.cmms.production.feignClients.NotifyFeignClient;
import com.cmms.production.feignClients.Production;
import com.cmms.production.repository.ProductRepository;
import com.cmms.production.user_context.UserContext;
import com.cmms.production.user_context.UserContextHolder;
import com.cmms.production.utils.ProductionOrderMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductOrderServiceImp implements ProductOrderService {
    private final ProductRepository productRepository;
    private final Production productionClient;
    private final ProductionOrderMapper productionOrderMapper;
    private final AuditLogFeignClient feignClient;
    private final NotifyFeignClient notifyFeignClient;
    private final ObjectMapper objectMapper;
    @Override
    @Transactional
    public ProductResponseDto saveProduct(ProductRequestDto requestDto) {
        if (requestDto == null || requestDto.getPlantId() == null || requestDto.getCarModelId() == null) {
            throw new IllegalArgumentException("IDs must not be null in the request data.");
        }

        Boolean plantExists = productionClient.existsPlantById(requestDto.getPlantId());
        Boolean carModelExists = productionClient.existsCarModelById(requestDto.getCarModelId());

        if (Boolean.FALSE.equals(plantExists)) {
            throw new IllegalArgumentException("Plant ID " + requestDto.getPlantId() + " does not exist in master data.");
        }
        if (Boolean.FALSE.equals(carModelExists)) {
            throw new IllegalArgumentException("Car Model ID " + requestDto.getCarModelId() + " does not exist in master data.");
        }
        PlantResponseDto plantResponseDto= productionClient.getPlantById(requestDto.getPlantId()).getBody();
        assert plantResponseDto != null;
        if(!Boolean.TRUE.equals(plantResponseDto.getIsActive())){
            throw new RuntimeException("Plant must be Active ");
        }

        CarModelResponseDto carModelResponseDto=productionClient.getCarModelById(requestDto.getCarModelId()).getBody();
        assert carModelResponseDto != null;
        if(!carModelResponseDto.isActive()){
            throw new RuntimeException("CarModel must be Active ");
        }

        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;

        ProductionOrder product = productionOrderMapper.mapToEntity(requestDto);
        String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String generatedCode = "PO-" + dateString + "-" + randomSuffix;
        product.setOrderNumber(generatedCode);
        product.setCreatedAt(LocalDateTime.now());
        product.setLastModifiedAt(LocalDateTime.now());
        product.setCreatedBy(userId);
        product.setLastModifiedBy(userId);
        ProductionOrder savedProduct = productRepository.save(product);
        try {
            sendSyncRequest(savedProduct, "CREATE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return productionOrderMapper.mapToResponseDto(savedProduct);
    }


    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        OrderStatus targetStatus = OrderStatus.valueOf(requestDto.getStatus());
        ProductionOrder existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));

        Boolean plantExists = productionClient.existsPlantById(requestDto.getPlantId());
        Boolean carModelExists = productionClient.existsCarModelById(requestDto.getCarModelId());

        if (Boolean.FALSE.equals(plantExists)) {
            throw new IllegalArgumentException("Plant ID " + requestDto.getPlantId() + " does not exist in master data.");
        }
        if (Boolean.FALSE.equals(carModelExists)) {
            throw new IllegalArgumentException("Car Model ID " + requestDto.getCarModelId() + " does not exist in master data.");
        }
        PlantResponseDto plantResponseDto= productionClient.getPlantById(requestDto.getPlantId()).getBody();
        assert plantResponseDto != null;
        if(!Boolean.TRUE.equals(plantResponseDto.getIsActive())){
            throw new RuntimeException("Plant must be Active ");
        }

        CarModelResponseDto carModelResponseDto=productionClient.getCarModelById(requestDto.getCarModelId()).getBody();
        assert carModelResponseDto != null;
        if(!carModelResponseDto.isActive()){
            throw new RuntimeException("CarModel must be Active ");
        }

        @NotNull OrderStatus currentStatus = existingProduct.getStatus();
        if (!currentStatus.isValidTransition(targetStatus)) {
            log.error("Invalid status transition attempted: {} → {}", currentStatus, targetStatus);
            throw new IllegalArgumentException("Invalid status transition: Cannot change status from "
                    + currentStatus + " to " + targetStatus);
        }
        existingProduct.setPlantId(requestDto.getPlantId());
        existingProduct.setCarModelId(requestDto.getCarModelId());
        existingProduct.setTargetQuantity(requestDto.getTargetQuantity());
        existingProduct.setStatus(OrderStatus.valueOf(requestDto.getStatus()));
        existingProduct.setStatus(targetStatus);

        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;
        existingProduct.setLastModifiedAt(LocalDateTime.now());
        existingProduct.setLastModifiedBy(userId);
        ProductionOrder updatedProduct = productRepository.save(existingProduct);
        try {
            sendNotifyRequest();
            sendSyncRequest(updatedProduct, "UPDATE");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return productionOrderMapper.mapToResponseDto(updatedProduct);
    }

    @Override

    public ProductResponseDto getById(Long id) {
        ProductionOrder product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("product not found with ID: " + id));
        return productionOrderMapper.mapToResponseDto(product);
    }

    @Override

    public List<ProductResponseDto> getAll() {
        return productRepository.findAll()
                .stream()
                .map(productionOrderMapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        ProductionOrder productionOrder = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot delete. Production Order not found with ID: " + id));

        try {
            sendSyncRequest(productionOrder, "DELETE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        productRepository.deleteById(id);
    }

    private void sendSyncRequest(ProductionOrder productionOrder, String actionStatus) throws Exception {
        String jsonPayload = objectMapper.writeValueAsString(productionOrder);
        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;

        DataChangeEventDto dto = new DataChangeEventDto();
        dto.setAction(actionStatus);
        dto.setTableName("ProductionOder");
        dto.setChangedData(jsonPayload);
        dto.setPerformedBy(userId);
        dto.setRecordId(productionOrder.getId());
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

    private void sendNotifyRequest() throws Exception {
        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;
        String role=(context != null && context.getRoles() != null) ? context.getRoles() :null;
        log.info("Recipient role :{}",role);
        if (role != null && role.startsWith("ROLE_")) {
            role = role.substring(5);
        }
        log.info("Recipient role (cleaned for DB): {}", role);
        NotifyChangeEventDto dto = new NotifyChangeEventDto();
        dto.setRecipientRole(role);
        dto.setMessage("Production order status changed");
        dto.setRead(true);
        dto.setNotificationType("STATUS_CHANGE");
        dto.setCreatedBy(userId);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setLastModifiedBy(userId);
        dto.setLastModifiedAt(LocalDateTime.now());
        notifyFeignClient.createNotifications(dto);
    }
}
