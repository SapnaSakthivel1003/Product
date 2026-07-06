package com.cmms.production.service;

import com.cmms.production.dto.QualityInspectionRequestDto;
import com.cmms.production.dto.QualityInspectionResponseDto;

import java.util.List;

public interface QualityInspectionService {
    QualityInspectionResponseDto saveQualityInspection(QualityInspectionRequestDto requestDto);
    QualityInspectionResponseDto getById(Long id);
    List<QualityInspectionResponseDto> getAll();
    QualityInspectionResponseDto updateQualityInspection(Long id, QualityInspectionRequestDto requestDto);
    void deleteById(Long id);
}
