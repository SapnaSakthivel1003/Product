package com.CMMS.Production.Repository;

import com.CMMS.Production.Entity.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductionOrder,Long> {
}
