package com.erp.modules.procurement.controller;

import com.erp.modules.procurement.dto.CreatePORequest;
import com.erp.modules.procurement.entity.PurchaseOrder;
import com.erp.modules.procurement.service.ProcurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/procurement")
@RequiredArgsConstructor
public class ProcurementController {

    private final ProcurementService procurementService;

    @PostMapping("/orders")
    @PreAuthorize("hasAuthority('PROCUREMENT_CREATE_PO') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<PurchaseOrder> createPO(@RequestBody CreatePORequest request) {
        return ResponseEntity.ok(procurementService.createPO(request));
    }

    @PostMapping("/orders/{id}/approve")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PurchaseOrder> approvePO(@PathVariable Long id) {
        return ResponseEntity.ok(procurementService.approvePO(id));
    }
    
    @GetMapping("/orders/{id}")
    public ResponseEntity<PurchaseOrder> getPO(@PathVariable Long id) {
        return ResponseEntity.ok(procurementService.getPO(id));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<PurchaseOrder>> getAllOrders() {
        return ResponseEntity.ok(procurementService.getAllOrders());
    }
}
