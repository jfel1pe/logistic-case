package com.logistic.logistic.controller;

import com.logistic.logistic.dto.ProductTypeDTO;
import com.logistic.logistic.service.ProductTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-types")
public class ProductTypeController {

    private final ProductTypeService productTypeService;

    public ProductTypeController(ProductTypeService productTypeService) {
        this.productTypeService = productTypeService;
    }

    @PostMapping
    public ResponseEntity<ProductTypeDTO> createProductType(@RequestBody ProductTypeDTO productTypeDTO) {
        ProductTypeDTO created = productTypeService.createProductType(productTypeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ProductTypeDTO>> getAllProductTypes() {
        List<ProductTypeDTO> productTypes = productTypeService.getAllProductTypes();
        return ResponseEntity.ok(productTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductTypeDTO> getProductTypeById(@PathVariable Integer id) {
        ProductTypeDTO productType = productTypeService.getProductTypeById(id);
        return ResponseEntity.ok(productType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductTypeDTO> updateProductType(
            @PathVariable Integer id,
            @RequestBody ProductTypeDTO productTypeDTO) {
        ProductTypeDTO updated = productTypeService.updateProductType(id, productTypeDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductType(@PathVariable Integer id) {
        productTypeService.deleteProductType(id);
        return ResponseEntity.noContent().build();
    }
}
