package com.CMMS.Production.Repository;

import com.CMMS.Production.Entity.VehicleInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleInventoryRepository extends JpaRepository<VehicleInventory,Long> {
}
