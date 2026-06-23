package com.CMMS.Production.Controller;

import com.CMMS.Production.Dto.ProductRequestDto;
import com.CMMS.Production.Dto.ProductResponseDto;
import com.CMMS.Production.Dto.QualityInspectionRequestDto;
import com.CMMS.Production.Dto.QualityInspectionResponseDto;
import com.CMMS.Production.Service.ProductOrderService;
import com.CMMS.Production.Service.QualityInspectionService;
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
    public ResponseEntity<QualityInspectionResponseDto> createQualityInspection(@RequestBody(required = true) QualityInspectionRequestDto requestDto) {
        System.err.println(requestDto);
        QualityInspectionResponseDto savedQualityInspection = qualityInspectionService.saveQualityInspection(requestDto);
        return new ResponseEntity<>(savedQualityInspection, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QualityInspectionResponseDto> getQualityInspectionById(@PathVariable Long id) {
        QualityInspectionResponseDto qualityInspection = qualityInspectionService.getById(id);
        return ResponseEntity.ok(qualityInspection);
    }


    @GetMapping
    public ResponseEntity<List<QualityInspectionResponseDto>> getAllQualityInspection() {
        return ResponseEntity.ok(qualityInspectionService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<QualityInspectionResponseDto> updateQualityInspection(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody QualityInspectionRequestDto requestDto) {
        System.out.println(userId);
        QualityInspectionResponseDto updatedQualityInspection = qualityInspectionService.updateQualityInspection(id, requestDto);
        return ResponseEntity.ok(updatedQualityInspection);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        qualityInspectionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
