package com.erp.modules.manufacturing.service;

import com.erp.modules.manufacturing.dto.CreateWorkOrderRequest;
import com.erp.modules.manufacturing.entity.*;
import com.erp.modules.manufacturing.repository.BOMRepository;
import com.erp.modules.manufacturing.repository.WorkOrderRepository;
import com.erp.modules.procurement.entity.Item;
import com.erp.modules.procurement.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManufacturingService {

    private final WorkOrderRepository workOrderRepository;
    private final BOMRepository bomRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public WorkOrder createWorkOrder(CreateWorkOrderRequest request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Find BOM
        BOM bom = bomRepository.findByItem(item)
                .orElseThrow(() -> new RuntimeException("BOM not found for item: " + item.getCode()));

        WorkOrder wo = new WorkOrder();
        wo.setItem(item);
        wo.setQuantity(request.getQuantity());
        wo.setStatus(WOStatus.PLANNED);
        
        if (request.getStartDate() != null) {
            wo.setStartDate(LocalDateTime.parse(request.getStartDate(), DateTimeFormatter.ISO_DATE_TIME));
        }
        if (request.getEndDate() != null) {
            wo.setEndDate(LocalDateTime.parse(request.getEndDate(), DateTimeFormatter.ISO_DATE_TIME));
        }

        // Calculate Requirements
        List<WorkOrderRequirement> requirements = new ArrayList<>();
        BigDecimal ratio = request.getQuantity().divide(bom.getBaseQuantity(), 4, RoundingMode.HALF_UP);

        for (BOMComponent component : bom.getComponents()) {
            WorkOrderRequirement req = new WorkOrderRequirement();
            req.setWorkOrder(wo);
            req.setItem(component.getItem());
            req.setRequiredQuantity(component.getQuantity().multiply(ratio));
            requirements.add(req);
        }

        wo.setRequirements(requirements);
        return workOrderRepository.save(wo);
    }

    @Transactional
    public WorkOrder updateStatus(Long id, WOStatus status) {
        WorkOrder wo = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work Order not found"));
        
        // Add validation logic here (e.g., can't go from PLANNED to COMPLETED directly)
        wo.setStatus(status);
        return workOrderRepository.save(wo);
    }
    
    public WorkOrder getWorkOrder(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work Order not found"));
    }

    public List<WorkOrder> getAllWorkOrders() {
        return workOrderRepository.findAll();
    }

    public List<Item> getItemsWithBOM() {
        return bomRepository.findAll().stream()
                .map(BOM::getItem)
                .toList();
    }
}
