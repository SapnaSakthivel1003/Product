package com.CMMS.Production.Entity;

import com.CMMS.Master.Data.Entity.CarModel;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="VehicleInventory")
@Data
public class VehicleInventory {
    @Id
    private long id;
    private String vin;
    @ManyToOne
    private ProductionOrder production_order;
    @ManyToOne
    private CarModel car_model;
    private String color;
    private enum Status {
        MANUFACTURED , INSPECTED , DELIVERED
    }
    @Column(name = "manufactured_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime manufactured_date;

}
