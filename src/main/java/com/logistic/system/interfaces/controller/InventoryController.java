package com.logistic.system.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.request.InventoryRequest;
import com.logistic.system.application.dto.response.InventoryResponse;
import com.logistic.system.application.service.InventoryApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Management", description = "Các API quản lý xuất nhập kho và kiểm tra tồn kho")
public class InventoryController {

    private final InventoryApplicationService inventoryApplicationService;

    @Operation(summary = "Trừ tồn kho", description = "Giảm số lượng sản phẩm trong kho khi có đơn hàng hoặc xuất kho thủ công")
    @PutMapping("/decrease")
    public ResponseEntity<String> decreStock(@Valid @RequestBody InventoryRequest request) {
        inventoryApplicationService.reduceStock(request);
        return ResponseEntity.ok("Trừ tồn kho thành công");
    }

    @Operation(summary = "Cộng tồn kho", description = "Tăng số lượng sản phẩm trong kho khi nhập hàng hoặc hoàn hàng")
    @PutMapping("/increase")
    public ResponseEntity<String> increStock(@Valid @RequestBody InventoryRequest request) {
        inventoryApplicationService.addStock(request);
        return ResponseEntity.ok("Hoàn tồn kho thành công");
    }

    @Operation(summary = "Kiểm tra tồn kho", description = "Lấy số lượng tồn kho hiện tại của một sản phẩm cụ thể")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tìm thấy thông tin tồn kho"),
            @ApiResponse(responseCode = "404", description = "Sản phẩm không tồn tại trong kho")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getStock(@PathVariable Long productId) {
        InventoryResponse response = inventoryApplicationService.getInventoryStockByProductId(productId);
        return ResponseEntity.ok(response);
    }
}
