package com.cmms.production.controller;

import com.cmms.production.dto.QualityInspectionRequestDto;
import com.cmms.production.dto.QualityInspectionResponseDto;
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
    public ResponseEntity<QualityInspectionResponseDto> createQualityInspection(@RequestBody(required = true) QualityInspectionRequestDto requestDto) {

        QualityInspectionResponseDto savedQualityInspection = qualityInspectionService.saveQualityInspection(requestDto);
        return new ResponseEntity<>(savedQualityInspection, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER"})
    public ResponseEntity<QualityInspectionResponseDto> getQualityInspectionById(@PathVariable Long id) {
        QualityInspectionResponseDto qualityInspection = qualityInspectionService.getById(id);
        return ResponseEntity.ok(qualityInspection);
    }
    @GetMapping
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER"})
    public ResponseEntity<List<QualityInspectionResponseDto>> getAllQualityInspection() {
        return ResponseEntity.ok(qualityInspectionService.getAll());
    }

    @PutMapping("/{id}")
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER","ROLE_SUPERVISOR"})
    public ResponseEntity<QualityInspectionResponseDto> updateQualityInspection(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody QualityInspectionRequestDto requestDto) {

        QualityInspectionResponseDto updatedQualityInspection = qualityInspectionService.updateQualityInspection(id, requestDto);
        return ResponseEntity.ok(updatedQualityInspection);
    }

    @DeleteMapping("/{id}")
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        qualityInspectionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
