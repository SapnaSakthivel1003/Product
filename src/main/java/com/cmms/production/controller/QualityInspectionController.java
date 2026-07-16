package com.cmms.production.controller;

import com.cmms.production.dto.QualityInspectionRequestDto;
import com.cmms.production.dto.QualityInspectionResponseDto;
import com.cmms.production.exception_handler.ApiResponse;
import com.cmms.production.service.QualityInspectionService;
import com.cmms.production.user_context.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/production/Quality")
@RequiredArgsConstructor
public class QualityInspectionController {
    private final QualityInspectionService qualityInspectionService;
    @PostMapping
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER","ROLE_SUPERVISOR"})
    public ResponseEntity<ApiResponse<QualityInspectionResponseDto>> createQualityInspection(@RequestBody(required = true) QualityInspectionRequestDto requestDto) {

        QualityInspectionResponseDto savedQualityInspection = qualityInspectionService.saveQualityInspection(requestDto);
        ApiResponse<QualityInspectionResponseDto> response = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "QualityInspection created successfully.",
                savedQualityInspection
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER"})
    public ResponseEntity<ApiResponse<QualityInspectionResponseDto>> getQualityInspectionById(@PathVariable Long id) {
        QualityInspectionResponseDto qualityInspection = qualityInspectionService.getById(id);
        ApiResponse<QualityInspectionResponseDto> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "QualityInspection retrieved successfully.",
                qualityInspection
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER"})
    public ResponseEntity<ApiResponse<List<QualityInspectionResponseDto>>> getAllQualityInspection() {
        List<QualityInspectionResponseDto> qualityInspection = qualityInspectionService.getAll();
        ApiResponse<List<QualityInspectionResponseDto>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "All QualityInspection retrieved successfully.",
                qualityInspection
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER","ROLE_SUPERVISOR"})
    public ResponseEntity<ApiResponse<QualityInspectionResponseDto>> updateQualityInspection(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody QualityInspectionRequestDto requestDto) {

        QualityInspectionResponseDto updatedQualityInspection = qualityInspectionService.updateQualityInspection(id, requestDto);
        ApiResponse<QualityInspectionResponseDto> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "QualityInspection updated successfully.",
                updatedQualityInspection
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        qualityInspectionService.deleteById(id);
        ApiResponse<Void> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "QualityInspection deleted successfully.",
                null
        );
        return ResponseEntity.ok(response);
    }
}
