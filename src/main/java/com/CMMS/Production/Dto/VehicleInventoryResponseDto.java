package com.CMMS.Production.Dto;

import com.CMMS.Master.Data.Entity.CarModel;
import com.CMMS.Production.Entity.ProductionOrder;
import com.CMMS.Production.Entity.Status;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Data
public class VehicleInventoryResponseDto {

    private String vin;
    private ProductionOrder productionOrder;
    private CarModel carModel;
    private String color;
    private Status status;
    private LocalDate manufacturedDate;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime lastModifiedAt;
    private Long lastModifiedBy;
}
