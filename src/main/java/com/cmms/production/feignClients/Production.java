package com.cmms.production.feignClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "master-service", url = "http://localhost:8081/api/v1",configuration =FeignClientSecurityConfig.class)
public interface Production {


    @GetMapping("/master-data/carModel/exists/{id}")
    Boolean existsCarModelById(@PathVariable("id") Long id);

    @GetMapping("/master-data/plant/exists/{id}")
    Boolean existsPlantById(@PathVariable("id") Long id);

    @GetMapping("/master-data/employee/exists/{id}")
    Boolean existsEmployeeById(@PathVariable("id") Long id);
}
