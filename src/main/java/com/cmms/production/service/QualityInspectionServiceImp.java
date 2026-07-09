package com.cmms.production.service;

import com.cmms.production.dto.*;
import com.cmms.production.entity.OrderStatus;
import com.cmms.production.entity.ProductionOrder;
import com.cmms.production.entity.QualityInspection;
import com.cmms.production.entity.Result;
import com.cmms.production.feignClients.AuditLogFeignClient;
import com.cmms.production.feignClients.NotifyFeignClient;
import com.cmms.production.feignClients.Production;
import com.cmms.production.feignClients.UserClient;
import com.cmms.production.repository.ProductRepository;
import com.cmms.production.repository.QualityInspectionRepository;
import com.cmms.production.user_context.UserContext;
import com.cmms.production.user_context.UserContextHolder;
import com.cmms.production.utils.QualityInspectionMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
public class QualityInspectionServiceImp implements QualityInspectionService{
    private final ProductRepository productRepository;
    private final QualityInspectionRepository qualityInspectionRepository;
    private final QualityInspectionMapper mapper;
    private final Production productionClient;
    private final AuditLogFeignClient feignClient;
    private final ObjectMapper objectMapper;
    private final NotifyFeignClient notifyFeignClient;
    private final UserClient userClient;
   private final EmailNotificationService emailNotificationService;
    @Override
    public QualityInspectionResponseDto saveQualityInspection(QualityInspectionRequestDto requestDto) {
        if (requestDto == null || requestDto.getInspectorId() == null || requestDto.getProductionOrderId()==null) {
            throw new IllegalArgumentException("ID's must not be null in the request data.");
        }
        Boolean employeeExists = productionClient.existsEmployeeById(requestDto.getInspectorId());

        if (Boolean.FALSE.equals(employeeExists)) {
            throw new IllegalArgumentException("Plant ID " + requestDto.getInspectorId() + " does not exist in master data.");
        }
        if (!productRepository.existsById(requestDto.getProductionOrderId())) {
            throw new IllegalArgumentException("Foreign key violation: Plant ID  does not exist.");
        }
        EmployeeResponseDto employeeResponseDto=productionClient.getEmployeeById(requestDto.getInspectorId()).getBody();
        assert employeeResponseDto != null;
        if(Boolean.FALSE.equals(employeeResponseDto.getIsActive())){
            throw new RuntimeException("Employee must be Active ");
        }
        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;

        QualityInspection qualityInspection = mapper.mapToEntity(requestDto);
        String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String generatedCode = "QI-" + dateString + "-" + randomSuffix;
        qualityInspection.setInspectionNumber(generatedCode);
        qualityInspection.setCreatedAt(LocalDateTime.now());
        qualityInspection.setLastModifiedAt(LocalDateTime.now());
        qualityInspection.setCreatedBy(userId);
        qualityInspection.setLastModifiedBy(userId);
        QualityInspection savedQualityInspection = qualityInspectionRepository.save(qualityInspection);

        try {
            sendSyncRequest(savedQualityInspection, "CREATE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mapper.mapToResponseDto(savedQualityInspection);
    }

    @Override
    public QualityInspectionResponseDto updateQualityInspection(Long id, QualityInspectionRequestDto requestDto) {
        QualityInspection existingQualityInspection = qualityInspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));

        String oldResult = String.valueOf(existingQualityInspection.getInspectionResult());
        Boolean employeeExists = productionClient.existsEmployeeById(requestDto.getInspectorId());

        if (Boolean.FALSE.equals(employeeExists)) {
            throw new IllegalArgumentException("employeeExists ID " + requestDto.getInspectorId() + " does not exist in master data.");
        }
        if (!productRepository.existsById(requestDto.getProductionOrderId())) {
            throw new IllegalArgumentException("Foreign key violation: production ID  does not exist.");
        }
        EmployeeResponseDto employeeResponseDto=productionClient.getEmployeeById(requestDto.getInspectorId()).getBody();
        assert employeeResponseDto != null;
        if(Boolean.FALSE.equals(employeeResponseDto.getIsActive())){
            throw new RuntimeException("Employee must be Active ");
        }
        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;
        existingQualityInspection.setInspectionResult(requestDto.getInspectionResult());
        existingQualityInspection.setLastModifiedAt(LocalDateTime.now());
        existingQualityInspection.setLastModifiedBy(userId);
        QualityInspection savedQualityInspection = qualityInspectionRepository.save(existingQualityInspection);
        try {
            sendSyncRequest(savedQualityInspection, "UPDATE");

            String newResult = String.valueOf(savedQualityInspection.getInspectionResult());

            if ("FAIL".equals(newResult) && !"FAIL".equals(oldResult)){
                log.info(">>> [QUALITY] State transitioned to FAIL. Triggering Plant Manager notifications.");
                sendNotifyRequest("PLANT_MANAGER", savedQualityInspection);


                List<String> plantManagerEmails = List.of("sapnasakthivel794@gmail.com", "6036sapna@gmail.com");
                for (String email : plantManagerEmails) {
                    emailNotificationService.sendFailureEmail(
                            email,
                            existingQualityInspection.getProductionOrderId(),
                            existingQualityInspection.getInspectionNumber()

                    );
                }

            } else {
                log.info(">>> [QUALITY] Notification skipped. Previous: {}, Current: {}", oldResult, newResult);
            }

        } catch (Exception e) {
            log.error("Failed to process post-update lifecycle operations: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return mapper.mapToResponseDto(savedQualityInspection);
    }

    @Override

    public QualityInspectionResponseDto getById(Long id) {
        QualityInspection qualityInspection = qualityInspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("product not found with ID: " + id));
        return mapper.mapToResponseDto(qualityInspection);
    }

    @Override

    public List<QualityInspectionResponseDto> getAll() {
        return qualityInspectionRepository.findAll()
                .stream()
                .map(mapper::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        QualityInspection qualityInspection = qualityInspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot delete. Production Order not found with ID: " + id));

        try {
            sendSyncRequest(qualityInspection, "DELETE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        qualityInspectionRepository.deleteById(id);
    }

    private void sendSyncRequest(QualityInspection qualityInspection, String actionStatus) throws Exception {
        String jsonPayload = objectMapper.writeValueAsString(qualityInspection);
        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;

        DataChangeEventDto dto = new DataChangeEventDto();
        dto.setAction(actionStatus);
        dto.setTableName("QualityInspection");
        dto.setChangedData(jsonPayload);
        dto.setPerformedBy(userId);
        dto.setRecordId(qualityInspection.getId());
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
    private void sendNotifyRequest(String role,QualityInspection savedQualityInspection)  {
        UserContext context = UserContextHolder.getContext();
        Long userId = (context != null && context.getUserId() != null) ? context.getUserId() : 0L;
        String message="Order number of the Product is :"+savedQualityInspection.getProductionOrderId()+"and inpectiom id is :"+savedQualityInspection.getInspectionNumber();
        NotifyChangeEventDto dto = new NotifyChangeEventDto();
        dto.setRecipientRole(role);
        dto.setMessage(message);
        dto.setRead(false);
        dto.setNotificationType("QC_FAIL");
        dto.setCreatedBy(userId);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setLastModifiedBy(userId);
        dto.setLastModifiedAt(LocalDateTime.now());
        notifyFeignClient.createNotifications(dto);
    }

}
