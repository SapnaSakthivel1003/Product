package com.CMMS.Production.Entity;

import com.CMMS.Master.Data.Entity.CarModel;
import com.CMMS.Master.Data.Entity.Plants;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "production_orders")
@Data
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ProductionOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber; // Format: PO-YYYYMMDD-NNNN

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plants plantId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_model_id", nullable = false)
    private CarModel carModelId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @NotNull
    @Min(value = 1, message = "Target quantity must be greater than 0")
    @Column(name = "target_quantity", nullable = false)
    private Integer targetQuantity;

    @NotNull
    @Column(name = "completed_quantity", nullable = false)
    private Integer completedQuantity = 0;

    @NotNull
    @Column(name = "expected_end_date", nullable = false)
    private LocalDate expectedEndDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    // --- Auditing Fields (Handled automatically by Spring Data JPA) ---

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;


    @LastModifiedDate
    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;

    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = false)
    private Long lastModifiedBy;

}
