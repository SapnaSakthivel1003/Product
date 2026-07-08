package com.cmms.production.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotifyChangeEventDto {
    private String recipientRole;
    private String message;
    private String notificationType;
    private boolean isRead;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime lastModifiedAt;
    private Long lastModifiedBy;
}
