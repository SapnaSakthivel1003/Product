package com.cmms.production.service;

import com.cmms.production.dto.ProductRequestDto;
import com.cmms.production.dto.ProductResponseDto;

import java.util.List;

public interface ProductOrderService {
    ProductResponseDto saveProduct(ProductRequestDto requestDto);
    ProductResponseDto getById(Long id);
    List<ProductResponseDto> getAll();
    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);
    void deleteById(Long id);
}
