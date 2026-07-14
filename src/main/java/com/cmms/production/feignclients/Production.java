package com.cmms.production.feignclients;


import com.cmms.production.dto.ExistenceResponseDto;
import com.cmms.production.exception_handler.CarModelApiResponse;
import com.cmms.production.exception_handler.EmployeeApiResponse;
import com.cmms.production.exception_handler.PlantApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "master-service", url = "http://localhost:8081/api/v1",configuration =FeignClientSecurityConfig.class)
public interface Production {


    @GetMapping("/master-data/carModel/exists/{id}")
    ExistenceResponseDto existsCarModelById(@PathVariable("id") Long id);

    @GetMapping("/master-data/plant/exists/{id}")
    ResponseEntity<ExistenceResponseDto> existsPlantById(@PathVariable("id") Long id);

    @GetMapping("/master-data/employee/exists/{id}")
    ExistenceResponseDto existsEmployeeById(@PathVariable("id") Long id);

    @GetMapping("/master-data/employee/{id}")
    ResponseEntity<EmployeeApiResponse> getEmployeeById(@PathVariable("id") long id);

    @GetMapping("/master-data/plant/{id}")
    ResponseEntity<PlantApiResponse> getPlantById(@PathVariable("id") long id);

    @GetMapping("/master-data/carModel/{id}")
    ResponseEntity<CarModelApiResponse> getCarModelById(@PathVariable("id") long id);

}
