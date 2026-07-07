package com.cmms.production.controller;

import com.cmms.production.dto.VehicleInventoryRequestDto;
import com.cmms.production.dto.VehicleInventoryResponseDto;
import com.cmms.production.repository.VehicleInventoryRepository;
import com.cmms.production.service.VehicleInventoryService;
import com.cmms.production.user_context.RequireRole;
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
    private final VehicleInventoryRepository vehicleInventoryRepository;
    @PostMapping
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<VehicleInventoryResponseDto> createVehicleInventory(@RequestBody(required = true) VehicleInventoryRequestDto requestDto) {
        VehicleInventoryResponseDto savedVehicleInventory = vehicleInventoryService.saveVehicleInventory(requestDto);
        return new ResponseEntity<>(savedVehicleInventory, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @RequireRole({"ROLE_ADMIN","ROLE_SUPERVISOR"})
    public ResponseEntity<VehicleInventoryResponseDto> getVehicleInventoryById(@PathVariable Long id) {
        VehicleInventoryResponseDto vehicleInventory = vehicleInventoryService.getById(id);
        return ResponseEntity.ok(vehicleInventory);
    }


    @GetMapping
    @RequireRole({"ROLE_ADMIN","ROLE_SUPERVISOR"})
    public ResponseEntity<List<VehicleInventoryResponseDto>> getAllVehicleInventory() {
        return ResponseEntity.ok(vehicleInventoryService.getAll());
    }

    @PutMapping("/{id}")
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<VehicleInventoryResponseDto> updateVehicleInventory(
            @PathVariable Long id,
            @Valid @RequestBody VehicleInventoryRequestDto requestDto) {
        VehicleInventoryResponseDto updatedVehicleInventory = vehicleInventoryService.updateVehicleInventory(id, requestDto);
        return ResponseEntity.ok(updatedVehicleInventory);
    }

    @DeleteMapping("/{id}")
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        vehicleInventoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsVehicleInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleInventoryRepository.existsById(id));
    }
}
