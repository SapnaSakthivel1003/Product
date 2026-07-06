package com.cmms.production.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataChangeEventDto {
    private String status;
    private String entityName;
    private String changedData;
}
