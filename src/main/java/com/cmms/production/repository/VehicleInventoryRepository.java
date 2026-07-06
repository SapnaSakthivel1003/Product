package com.cmms.production.repository;

import com.cmms.production.entity.VehicleInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleInventoryRepository extends JpaRepository<VehicleInventory,Long> {
}
