package com.cmms.production.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "production_orders",schema = "CmmsTables")
@Data
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@SoftDelete(columnName = "is_deleted")
public class ProductionOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column( unique = true, nullable = false)
    private String orderNumber;
    @NotNull
    private Long plantId;

    @NotNull(message = "Car Model ID is required")
    @Column(nullable = false)
    private Long carModelId;

    @NotNull

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @Min(value = 1, message = "Target quantity must be greater than 0")
    @Column( nullable = false)
    private Integer targetQuantity;

    @NotNull
    @Column(nullable = false)
    private Integer completedQuantity = 0;

    @NotNull
    @Column( nullable = false)
    private LocalDate expectedEndDate;

    private LocalDate actualEndDate;

    // --- Auditing Fields (Handled automatically by Spring data JPA) ---

    @CreatedDate
    @Column( nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column( nullable = false, updatable = false)
    private Long createdBy;


    @LastModifiedDate
    @Column( nullable = false)
    private LocalDateTime lastModifiedAt;

    @LastModifiedBy
    @Column( nullable = false)
    private Long lastModifiedBy;

}
