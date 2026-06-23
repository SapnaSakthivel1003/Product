package com.CMMS.Production.Entity;

import com.CMMS.Master.Data.Entity.Employee;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="QualityInspection")
@Data
public class QualityInspection {
    @Id
    private long id;
    private String inspection_number;
    @OneToOne
    private ProductionOrder production_order;
    @ManyToOne
    private Employee inspector;
    private enum Result {
        PASS , FAIL ,PENDING
    }
    private Result inspection_result;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String remarks;
    @Column(name = "inspected_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime inspected_at;

}
