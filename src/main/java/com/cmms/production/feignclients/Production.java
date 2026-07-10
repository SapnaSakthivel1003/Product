package com.cmms.production.feignclients;

import com.cmms.production.dto.CarModelResponseDto;
import com.cmms.production.dto.EmployeeResponseDto;
import com.cmms.production.dto.PlantResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "master-service", url = "http://localhost:8081/api/v1",configuration =FeignClientSecurityConfig.class)
public interface Production {


    @GetMapping("/master-data/carModel/exists/{id}")
    Boolean existsCarModelById(@PathVariable("id") Long id);

    @GetMapping("/master-data/plant/exists/{id}")
    Boolean existsPlantById(@PathVariable("id") Long id);

    @GetMapping("/master-data/employee/exists/{id}")
    Boolean existsEmployeeById(@PathVariable("id") Long id);

    @GetMapping("/master-data/employee/{id}")
    ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable("id") long id);

    @GetMapping("/master-data/plant/{id}")
    ResponseEntity<PlantResponseDto> getPlantById(@PathVariable("id") long id);

    @GetMapping("/master-data/carModel/{id}")
    ResponseEntity<CarModelResponseDto> getCarModelById(@PathVariable("id") long id);

}
