package com.cmms.production.dto;

import lombok.Data;

@Data
public class PlantResponseDto {
    private Long id;
    private String name;
    private String code;
    private String location;
    private Integer capacityPerDay;
    private Boolean isActive;
}
