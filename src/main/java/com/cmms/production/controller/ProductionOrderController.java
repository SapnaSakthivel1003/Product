package com.cmms.production.controller;

import com.cmms.production.dto.ProductRequestDto;
import com.cmms.production.dto.ProductResponseDto;
import com.cmms.production.exception_handler.ApiResponse;
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
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(@RequestBody(required = true) ProductRequestDto requestDto) {
        ProductResponseDto savedProduct = productOrderService.saveProduct(requestDto);
        ApiResponse<ProductResponseDto> response = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "ProductOrder created successfully.",
                savedProduct
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER","ROLE_SUPERVISOR"})
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productOrderService.getById(id);
        ApiResponse<ProductResponseDto> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "ProductOrder retrieved successfully.",
                product
        );
        return ResponseEntity.ok(response);
    }


    @GetMapping
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER","ROLE_SUPERVISOR"})
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getAllProduct() {
        List<ProductResponseDto> customer = productOrderService.getAll();
        ApiResponse<List<ProductResponseDto>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "All ProductOrder retrieved successfully.",
                customer
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @RequireRole({"ROLE_ADMIN", "ROLE_PLANT_MANAGER","ROLE_SUPERVISOR"})
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ProductRequestDto requestDto) {
        ProductResponseDto updatedProduct = productOrderService.updateProduct(id, requestDto);
        ApiResponse<ProductResponseDto> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "ProductOrder updated successfully.",
                updatedProduct
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @RequireRole("ROLE_ADMIN")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productOrderService.deleteById(id);
        ApiResponse<Void> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "ProductOrder deleted successfully.",
                null
        );
        return ResponseEntity.ok(response);
    }
}
