package com.CMMS.Production.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="ProductionOrder")
@Data
public class ProductionOrder {
    @Id
    private long id;
    private String order_number;
//    @ManyToOne
//    private Plants plant;
//    @ManyToOne
//    private CarModel car_model;
    private enum Status {
        PENDING , IN_PROGRESS , COMPLETED , CANCELLED
    }
    private Status status;
    private int target_quantity;
    private int completed_quantity;
    @Column(name = "expected_end_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime expected_end_date;
    @Column(name = "actual_end_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime actual_end_date;


}
