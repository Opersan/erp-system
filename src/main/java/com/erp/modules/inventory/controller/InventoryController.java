package com.erp.modules.inventory.controller;

import com.erp.modules.inventory.dto.GoodsReceiptRequest;
import com.erp.modules.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/receipts")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Or specific permission
    public ResponseEntity<String> receiveGoods(@RequestBody GoodsReceiptRequest request) {
        inventoryService.processGoodsReceipt(request);
        return ResponseEntity.ok("Goods received successfully");
    }
}
