package com.erp.modules.manufacturing.controller;

import com.erp.modules.manufacturing.dto.CreateWorkOrderRequest;
import com.erp.modules.manufacturing.entity.WOStatus;
import com.erp.modules.manufacturing.entity.WorkOrder;
import com.erp.modules.manufacturing.service.ManufacturingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manufacturing")
@RequiredArgsConstructor
public class ManufacturingController {

    private final ManufacturingService manufacturingService;

    @PostMapping("/work-orders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<WorkOrder> createWorkOrder(@RequestBody CreateWorkOrderRequest request) {
        return ResponseEntity.ok(manufacturingService.createWorkOrder(request));
    }

    @PutMapping("/work-orders/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<WorkOrder> updateStatus(@PathVariable Long id, @RequestParam WOStatus status) {
        return ResponseEntity.ok(manufacturingService.updateStatus(id, status));
    }
    
    @GetMapping("/work-orders/{id}")
    public ResponseEntity<WorkOrder> getWorkOrder(@PathVariable Long id) {
        return ResponseEntity.ok(manufacturingService.getWorkOrder(id));
    }

    @GetMapping("/work-orders")
    public ResponseEntity<List<WorkOrder>> getAllWorkOrders() {
        return ResponseEntity.ok(manufacturingService.getAllWorkOrders());
    }
}
