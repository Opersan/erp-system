package com.erp.modules.mrp.service;

import com.erp.modules.inventory.entity.Stock;
import com.erp.modules.inventory.repository.StockRepository;
import com.erp.modules.manufacturing.entity.WorkOrderRequirement;
import com.erp.modules.manufacturing.repository.WorkOrderRequirementRepository;
import com.erp.modules.mrp.entity.MrpRun;
import com.erp.modules.mrp.entity.PlannedOrder;
import com.erp.modules.mrp.entity.PlannedOrderStatus;
import com.erp.modules.mrp.repository.MrpRunRepository;
import com.erp.modules.mrp.repository.PlannedOrderRepository;
import com.erp.modules.procurement.dto.CreatePORequest;
import com.erp.modules.procurement.dto.PurchaseOrderItemDto;
import com.erp.modules.procurement.entity.Item;
import com.erp.modules.procurement.entity.PurchaseOrder;
import com.erp.modules.procurement.entity.Supplier;
import com.erp.modules.procurement.repository.ItemRepository;
import com.erp.modules.procurement.repository.SupplierRepository;
import com.erp.modules.procurement.service.ProcurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MrpService {

    private final MrpRunRepository mrpRunRepository;
    private final PlannedOrderRepository plannedOrderRepository;
    private final WorkOrderRequirementRepository workOrderRequirementRepository;
    private final StockRepository stockRepository;
    private final ItemRepository itemRepository;
    private final ProcurementService procurementService;
    private final SupplierRepository supplierRepository;

    @Transactional
    public MrpRun runMrp(int horizonDays) {
        LocalDateTime horizonDate = LocalDateTime.now().plusDays(horizonDays);
        
        MrpRun run = new MrpRun();
        run.setRunDate(LocalDateTime.now());
        run.setHorizonDays(horizonDays);
        run = mrpRunRepository.save(run);

        // 1. Calculate Demand (from Work Orders)
        List<WorkOrderRequirement> requirements = workOrderRequirementRepository.findRequirementsWithinHorizon(horizonDate);
        Map<Long, BigDecimal> demandMap = new HashMap<>(); // ItemId -> Total Demand

        for (WorkOrderRequirement req : requirements) {
            demandMap.merge(req.getItem().getId(), req.getRequiredQuantity(), BigDecimal::add);
        }

        // 2. Calculate Net Requirements & Create Planned Orders
        List<Item> allItems = itemRepository.findAll();
        
        for (Item item : allItems) {
            BigDecimal demand = demandMap.getOrDefault(item.getId(), BigDecimal.ZERO);
            
            // Get Total Stock (Sum across all warehouses for simplicity)
            // In real world, we should consider warehouse locations.
            List<Stock> stocks = stockRepository.findAll().stream()
                    .filter(s -> s.getItem().getId().equals(item.getId()))
                    .collect(Collectors.toList());
            
            BigDecimal onHand = stocks.stream()
                    .map(s -> BigDecimal.valueOf(s.getOnHand()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Net Requirement = Demand - OnHand
            // (Ignoring Scheduled Receipts from existing POs for MVP simplicity, or assume they are 0)
            BigDecimal netRequirement = demand.subtract(onHand);

            if (netRequirement.compareTo(BigDecimal.ZERO) > 0) {
                PlannedOrder plannedOrder = new PlannedOrder();
                plannedOrder.setMrpRun(run);
                plannedOrder.setItem(item);
                plannedOrder.setQuantity(netRequirement);
                plannedOrder.setOrderDate(LocalDateTime.now()); // Immediate order needed
                plannedOrder.setStatus(PlannedOrderStatus.PROPOSED);
                plannedOrderRepository.save(plannedOrder);
            }
        }

        return run;
    }

    @Transactional
    public List<PurchaseOrder> createPosFromPlannedOrders(List<Long> plannedOrderIds) {
        List<PlannedOrder> plannedOrders = plannedOrderRepository.findAllById(plannedOrderIds);
        List<PurchaseOrder> createdPos = new ArrayList<>();

        // Group by Supplier (For MVP, just pick the first supplier found or a default one)
        // Since Item doesn't have a default supplier link in our simple model, 
        // let's assume all items come from the first supplier in DB for this demo.
        Supplier defaultSupplier = supplierRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No suppliers found to create PO"));

        // Group items into one PO per supplier (here just one PO for all)
        CreatePORequest poRequest = new CreatePORequest();
        poRequest.setSupplierId(defaultSupplier.getId());
        List<PurchaseOrderItemDto> items = new ArrayList<>();

        for (PlannedOrder po : plannedOrders) {
            if (po.getStatus() != PlannedOrderStatus.PROPOSED) {
                continue; // Skip already processed
            }

            PurchaseOrderItemDto itemDto = new PurchaseOrderItemDto();
            itemDto.setItemId(po.getItem().getId());
            itemDto.setQuantity(po.getQuantity().intValue()); // PO uses Integer, MRP uses BigDecimal. Casting for MVP.
            itemDto.setPrice(po.getItem().getPrice());
            items.add(itemDto);

            po.setStatus(PlannedOrderStatus.CONVERTED);
            plannedOrderRepository.save(po);
        }

        if (!items.isEmpty()) {
            poRequest.setItems(items);
            createdPos.add(procurementService.createPO(poRequest));
        }

        return createdPos;
    }
    
    public List<PlannedOrder> getPlannedOrders(Long runId) {
        MrpRun run = mrpRunRepository.findById(runId)
                .orElseThrow(() -> new RuntimeException("Run not found"));
        return plannedOrderRepository.findByMrpRun(run);
    }

    public List<MrpRun> getAllRuns() {
        return mrpRunRepository.findAll();
    }
}
