package com.CMMS.Production.Controller;

import com.CMMS.Production.Dto.QualityInspectionRequestDto;
import com.CMMS.Production.Dto.QualityInspectionResponseDto;
import com.CMMS.Production.Dto.VehicleInventoryRequestDto;
import com.CMMS.Production.Dto.VehicleInventoryResponseDto;
import com.CMMS.Production.Service.QualityInspectionService;
import com.CMMS.Production.Service.VehicleInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/production/vehicleInventory")
@RequiredArgsConstructor
public class VehicleInventoryController {
    private final VehicleInventoryService vehicleInventoryService;
    @PostMapping
    public ResponseEntity<VehicleInventoryResponseDto> createVehicleInventory(@RequestBody(required = true) VehicleInventoryRequestDto requestDto) {
        System.err.println(requestDto);
        VehicleInventoryResponseDto savedVehicleInventory = vehicleInventoryService.saveVehicleInventory(requestDto);
        return new ResponseEntity<>(savedVehicleInventory, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleInventoryResponseDto> getVehicleInventoryById(@PathVariable Long id) {
        VehicleInventoryResponseDto vehicleInventory = vehicleInventoryService.getById(id);
        return ResponseEntity.ok(vehicleInventory);
    }


    @GetMapping
    public ResponseEntity<List<VehicleInventoryResponseDto>> getAllVehicleInventory() {
        return ResponseEntity.ok(vehicleInventoryService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleInventoryResponseDto> updateVehicleInventory(
            @PathVariable Long id,
            @Valid @RequestBody VehicleInventoryRequestDto requestDto) {
        VehicleInventoryResponseDto updatedVehicleInventory = vehicleInventoryService.updateVehicleInventory(id, requestDto);
        return ResponseEntity.ok(updatedVehicleInventory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        vehicleInventoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
