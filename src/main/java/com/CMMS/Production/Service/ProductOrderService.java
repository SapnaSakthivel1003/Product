package com.CMMS.Production.Service;

import com.CMMS.Production.Dto.ProductRequestDto;
import com.CMMS.Production.Dto.ProductResponseDto;

import java.util.List;

public interface ProductOrderService {
    ProductResponseDto saveProduct(ProductRequestDto requestDto);
    ProductResponseDto getById(Long id);
    List<ProductResponseDto> getAll();
    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);
    void deleteById(Long id);
}
