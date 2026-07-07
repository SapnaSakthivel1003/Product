package com.cmms.production.repository;

import com.cmms.production.entity.QualityInspection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QualityInspectionRepository extends JpaRepository<QualityInspection,Long> {
}
