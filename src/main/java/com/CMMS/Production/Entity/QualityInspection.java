package com.CMMS.Production.Entity;

import com.CMMS.Master.Data.Entity.Employee;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="QualityInspection",schema = "CmmsTables")
@Data
public class QualityInspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inspectionNumber", nullable = false, unique = true, length = 50)
    private String inspectionNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productionOrderId", nullable = false, unique = true)
    private ProductionOrder productionOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspectorId", nullable = false)
    private Employee inspectorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "inspectionResult", nullable = false)
    private Result inspectionResult = Result.PENDING;

    @Column(columnDefinition = "text")
    private String remarks;

    @Column(name = "inspectedAt", nullable = false, updatable = false)
    private LocalDateTime inspectedAt;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "createdBy", nullable = false, updatable = false)
    private Long createdBy;

    @Column(name = "lastModifiedAt", nullable = false)
    private LocalDateTime lastModifiedAt;

    @Column(name = "lastModifiedBy", nullable = false)
    private Long lastModifiedBy;

}
