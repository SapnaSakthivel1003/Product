package com.cmms.production.dto;

import com.cmms.production.entity.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VehicleInventoryRequestDto {

    private String vin;
    private Long productionOrder;
    private Long carModel;
    private String color;
    private Status status;
    private LocalDate manufacturedDate;
}
