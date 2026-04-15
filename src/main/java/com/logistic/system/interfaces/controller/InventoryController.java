package com.logistic.system.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistic.system.application.dto.reponse.InventoryResponse;
import com.logistic.system.application.dto.request.InventoryRequest;
import com.logistic.system.application.service.InventoryApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryApplicationService inventoryApplicationService;

    /**
     * API trừ tồn kho
     */
    @PutMapping("/decrease")
    public ResponseEntity<String> decreStock(@RequestBody InventoryRequest request) {
        inventoryApplicationService.reduceStock(request);
        return ResponseEntity.ok("Trừ tồn kho thành công");
    }

    @PutMapping("/increase")
    public ResponseEntity<String> increStock(@RequestBody InventoryRequest request) {
        inventoryApplicationService.addStock(request);
        return ResponseEntity.ok("Hoàn tồn kho thành công");
    }

    /**
     * API xem thông tin tồn kho theo productId
     */
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getStock(@PathVariable Long productId) {
        InventoryResponse response = inventoryApplicationService.getInventoryStockByProductId(productId);
        return ResponseEntity.ok(response);
    }
}
