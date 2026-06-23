package com.CMMS.Production.Controller;

import com.CMMS.Production.Dto.ProductRequestDto;
import com.CMMS.Production.Dto.ProductResponseDto;
import com.CMMS.Production.Service.ProductOrderService;
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
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody(required = true) ProductRequestDto requestDto) {
        System.err.println(requestDto);
        ProductResponseDto savedProduct = productOrderService.saveProduct(requestDto);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        ProductResponseDto Product = productOrderService.getById(id);
        return ResponseEntity.ok(Product);
    }


    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProduct() {
        return ResponseEntity.ok(productOrderService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ProductRequestDto requestDto) {
        System.out.println(userId);
        ProductResponseDto updatedProduct = productOrderService.updateProduct(id, requestDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        productOrderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
