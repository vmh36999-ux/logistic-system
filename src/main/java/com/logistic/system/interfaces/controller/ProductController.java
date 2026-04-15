package com.logistic.system.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.reponse.ProductResponse;
import com.logistic.system.application.dto.request.ProductRequest;
import com.logistic.system.application.service.ProductApplicationService;
import com.logistic.system.infrastructure.mapper.ProductMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductApplicationService productApplicationService;
    private final ProductMapper productMapper;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        var product = productMapper.toProductRequestDomain(request);
        var saved = productApplicationService.saveProduct(product);
        return ResponseEntity.ok(productMapper.toResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        var product = productApplicationService.getProductForShipping(id);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }
}