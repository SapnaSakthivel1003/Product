package com.cmms.production.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataChangeEventDto {
    private String tableName;
    private Long recordId;
    private String action;
    private String changedData;
    private Long performedBy;
    private String ipAddress;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime lastModifiedAt;
    private Long lastModifiedBy;
}
