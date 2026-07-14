package com.cmms.production.controller;

import com.cmms.production.dto.VehicleInventoryRequestDto;
import com.cmms.production.dto.VehicleInventoryResponseDto;
import com.cmms.production.exception_handler.ApiResponse;
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
    public ResponseEntity<ApiResponse<VehicleInventoryResponseDto>> createVehicleInventory(@RequestBody(required = true) VehicleInventoryRequestDto requestDto) {
        VehicleInventoryResponseDto savedVehicleInventory = vehicleInventoryService.saveVehicleInventory(requestDto);
        ApiResponse<VehicleInventoryResponseDto> response = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "VehicleInventory created successfully.",
                savedVehicleInventory
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @RequireRole({"ROLE_ADMIN","ROLE_SUPERVISOR"})
    public ResponseEntity<ApiResponse<VehicleInventoryResponseDto>> getVehicleInventoryById(@PathVariable Long id) {
        VehicleInventoryResponseDto vehicleInventory = vehicleInventoryService.getById(id);
        ApiResponse<VehicleInventoryResponseDto> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "VehicleInventory retrieved successfully.",
                vehicleInventory
        );
        return ResponseEntity.ok(response);
    }


    @GetMapping
    @RequireRole({"ROLE_ADMIN","ROLE_SUPERVISOR"})
    public ResponseEntity<ApiResponse<List<VehicleInventoryResponseDto>>> getAllVehicleInventory() {
        List<VehicleInventoryResponseDto> customer = vehicleInventoryService.getAll();
        ApiResponse<List<VehicleInventoryResponseDto>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "All VehicleInventory retrieved successfully.",
                customer
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<ApiResponse<VehicleInventoryResponseDto>> updateVehicleInventory(
            @PathVariable Long id,
            @Valid @RequestBody VehicleInventoryRequestDto requestDto) {
        VehicleInventoryResponseDto updatedVehicleInventory = vehicleInventoryService.updateVehicleInventory(id, requestDto);
        ApiResponse<VehicleInventoryResponseDto> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "VehicleInventory updated successfully.",
                updatedVehicleInventory
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        vehicleInventoryService.deleteById(id);
        ApiResponse<Void> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "VehicleInventory deleted successfully.",
                null
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping("/exists/{id}")
    public ResponseEntity<ApiResponse<Boolean>> existsVehicleInventoryById(@PathVariable Long id) {
        Boolean exists = vehicleInventoryService.existsById(id);
        ApiResponse<Boolean> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "VehicleInventory existence check completed.",
                exists
        );
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        vehicleInventoryService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
