package com.CMMS.Production.Service;

import com.CMMS.Production.Dto.ProductRequestDto;
import com.CMMS.Production.Dto.ProductResponseDto;
import com.CMMS.Production.Dto.QualityInspectionRequestDto;
import com.CMMS.Production.Dto.QualityInspectionResponseDto;

import java.util.List;

public interface QualityInspectionService {
    QualityInspectionResponseDto saveQualityInspection(QualityInspectionRequestDto requestDto);
    QualityInspectionResponseDto getById(Long id);
    List<QualityInspectionResponseDto> getAll();
    QualityInspectionResponseDto updateQualityInspection(Long id, QualityInspectionRequestDto requestDto);
    void deleteById(Long id);
}
