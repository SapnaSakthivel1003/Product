package com.cmms.production.controller;

import com.cmms.production.dto.ProductRequestDto;
import com.cmms.production.dto.ProductResponseDto;
import com.cmms.production.service.ProductOrderService;

import com.cmms.production.user_context.RequireRole;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/production/order")
@Data
@RequiredArgsConstructor
public class ProductionOrderController {
    private final ProductOrderService productOrderService;

    @PostMapping
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER"})
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody(required = true) ProductRequestDto requestDto) {
        ProductResponseDto savedProduct = productOrderService.saveProduct(requestDto);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER","ROLE_SUPERVISOR"})
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productOrderService.getById(id);
        return ResponseEntity.ok(product);
    }


    @GetMapping
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER","ROLE_SUPERVISOR"})
    public ResponseEntity<List<ProductResponseDto>> getAllProduct() {
        return ResponseEntity.ok(productOrderService.getAll());
    }

    @PutMapping("/{id}")
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER","ROLE_SUPERVISOR"})
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ProductRequestDto requestDto) {
        ProductResponseDto updatedProduct = productOrderService.updateProduct(id, requestDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        productOrderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
