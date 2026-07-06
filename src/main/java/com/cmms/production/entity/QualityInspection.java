package com.cmms.production.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="QualityInspection",schema = "CmmsTables")
@Data
@SoftDelete(columnName = "is_deleted")
public class QualityInspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inspectionNumber", nullable = false, unique = true, length = 50)
    private String inspectionNumber;

    @Column(name = "production_order_id")
    private Long productionOrderId;

    @NotNull(message = "inspectorId is required")
    @Column(nullable = false)
    private Long inspectorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "inspectionResult", nullable = false)
    private Result inspectionResult = Result.PENDING;

    @Column(columnDefinition = "text")
    private String remarks;

    @Column(name = "inspectedAt", nullable = false, updatable = false)
    private LocalDate inspectedAt;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "createdBy", nullable = false, updatable = false)
    private Long createdBy;

    @Column(name = "lastModifiedAt", nullable = false)
    private LocalDateTime lastModifiedAt;

    @Column(name = "lastModifiedBy", nullable = false)
    private Long lastModifiedBy;

}
