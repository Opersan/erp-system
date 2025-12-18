package com.erp.modules.mrp.controller;

import com.erp.modules.mrp.entity.MrpRun;
import com.erp.modules.mrp.entity.PlannedOrder;
import com.erp.modules.mrp.service.MrpService;
import com.erp.modules.procurement.entity.PurchaseOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mrp")
@RequiredArgsConstructor
public class MrpController {

    private final MrpService mrpService;

    @PostMapping("/run")
    @PreAuthorize("hasAuthority('MRP_RUN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MrpRun> runMrp(@RequestParam(defaultValue = "30") int horizonDays) {
        return ResponseEntity.ok(mrpService.runMrp(horizonDays));
    }

    @GetMapping("/runs/{runId}/planned-orders")
    @PreAuthorize("hasAuthority('MRP_RUN') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<PlannedOrder>> getPlannedOrders(@PathVariable Long runId) {
        return ResponseEntity.ok(mrpService.getPlannedOrders(runId));
    }

    @PostMapping("/convert-to-po")
    @PreAuthorize("hasAuthority('PROCUREMENT_CREATE_PO') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<PurchaseOrder>> convertToPo(@RequestBody List<Long> plannedOrderIds) {
        return ResponseEntity.ok(mrpService.createPosFromPlannedOrders(plannedOrderIds));
    }

    @GetMapping("/runs")
    public ResponseEntity<List<MrpRun>> getAllRuns() {
        return ResponseEntity.ok(mrpService.getAllRuns());
    }
}
