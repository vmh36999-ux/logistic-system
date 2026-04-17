package com.logistic.system.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistic.system.application.dto.response.ProductResponse;
import com.logistic.system.domain.model.Product;
import com.logistic.system.domain.service.ProductDomainService;
import com.logistic.system.infrastructure.mapper.ProductMapper;
import com.logistic.system.infrastructure.persistence.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductApplicationService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductDomainService productDomainService;

    /**
     * Hàm quan trọng nhất để phục vụ module Shipment
     */
    @Transactional(readOnly = true)
    public Product getProductForShipping(Long productId) {
        // 1. Lấy Entity từ Database
        return productRepository.findByProductId(productId)
                // 2. Map sang Domain
                .map(productMapper::toDomain)
                // 3. Thực hiện kiểm tra luật nghiệp vụ (về cân nặng, thông tin...)
                .map(product -> {
                    productDomainService.validateForShipping(product);
                    return product;
                })
                // 4. Nếu không thấy sản phẩm thì quăng lỗi
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " +
                        productId));
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        return productRepository.findByProductId(productId)
                .map(productMapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(int page, int size, String sortBy, String direction) {
        // 1. Tạo đối tượng Sort
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        // 2. Tạo Pageable (Spring Data JPA bắt đầu từ trang 0)
        Pageable pageable = PageRequest.of(page, size, sort);

        // 3. Lấy Page Entity từ Repository
        var entitiesPage = productRepository.findAll(pageable);

        // 4. Map trực tiếp sang Page DTO (Trả về tầng Interface)
        return entitiesPage.map(productMapper::toResponseFromEntity);
    }
    public Page<ProductResponse> searchProducts(String search, int page, int size, String sortBy, String direction) {
        // 1. Tạo đối tượng Sort
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        // 2. Tạo Pageable (Spring Data JPA bắt đầu từ trang 0)
        Pageable pageable = PageRequest.of(page, size, sort);

        // 3. Lấy Page Entity từ Repository
        var entitiesPage = productRepository.searchProducts(search, pageable);

        // 4. Map trực tiếp sang Page DTO (Trả về tầng Interface)
        return entitiesPage.map(productMapper::toResponseFromEntity);
    }

    @Transactional
    public Product saveProduct(Product product) {
        productDomainService.validateForShipping(product);
        var entity = productMapper.toEntity(product);
        var saved = productRepository.save(entity);
        return productMapper.toDomain(saved);
    }

}