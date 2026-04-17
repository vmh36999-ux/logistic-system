package com.logistic.system.interfaces.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.request.ProductRequest;
import com.logistic.system.application.dto.response.ProductResponse;
import com.logistic.system.application.service.ProductApplicationService;
import com.logistic.system.infrastructure.mapper.ProductMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Các API quản lý thông tin sản phẩm và tồn kho")
public class ProductController {

    private final ProductApplicationService productApplicationService;
    private final ProductMapper productMapper;

    @Operation(summary = "Tạo sản phẩm mới", description = "Tạo một sản phẩm mới trong hệ thống với các thông tin cơ bản")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo sản phẩm thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "403", description = "Không có quyền thực hiện")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        var product = productMapper.toProductRequestDomain(request);
        var saved = productApplicationService.saveProduct(product);
        return ResponseEntity.ok(productMapper.toResponse(saved));
    }

    @Operation(summary = "Lấy chi tiết sản phẩm", description = "Lấy thông tin chi tiết của một sản phẩm dựa trên ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy sản phẩm"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        var product = productApplicationService.getProductForShipping(id);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    @Operation(summary = "Lấy danh sách sản phẩm", description = "Lấy danh sách sản phẩm có hỗ trợ tìm kiếm, phân trang và sắp xếp")
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productId") String sortByProductId,
            @RequestParam(defaultValue = "asc") String direction) {
        if (search != null && !search.isEmpty()) {
            return ResponseEntity
                    .ok(productApplicationService.searchProducts(search, page, size, sortByProductId, direction));
        }
        return ResponseEntity.ok(productApplicationService.getAllProducts(page, size, sortByProductId, direction));
    }
}