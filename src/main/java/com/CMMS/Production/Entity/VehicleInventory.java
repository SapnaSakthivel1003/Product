package com.CMMS.Production.Entity;

import com.CMMS.Master.Data.Entity.CarModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="VehicleInventory",schema = "CmmsTables")
@Data
public class VehicleInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 17, max = 17)
    @Column(length = 17, unique = true, updatable = false, nullable = false)
    private String vin;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "production_order_id", nullable = false)
    private ProductionOrder productionOrder;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_model_id", nullable = false)
    private CarModel carModel;

    @NotNull
    @Column(nullable = false)
    private String color;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @NotNull
    @Column(name = "manufactured_date", updatable = false, nullable = false)
    private LocalDate manufacturedDate;

    @NotNull
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;

    @NotNull
    @LastModifiedDate
    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;

    @NotNull
    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = false)
    private Long lastModifiedBy;
}
