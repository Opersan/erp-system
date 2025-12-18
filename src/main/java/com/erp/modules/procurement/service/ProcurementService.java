package com.erp.modules.procurement.service;

import com.erp.modules.procurement.dto.CreatePORequest;
import com.erp.modules.procurement.dto.PurchaseOrderItemDto;
import com.erp.modules.procurement.entity.*;
import com.erp.modules.procurement.repository.ItemRepository;
import com.erp.modules.procurement.repository.PurchaseOrderRepository;
import com.erp.modules.procurement.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcurementService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public PurchaseOrder createPO(CreatePORequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        PurchaseOrder po = new PurchaseOrder();
        po.setSupplier(supplier);
        po.setStatus(POStatus.DRAFT);
        
        List<PurchaseOrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PurchaseOrderItemDto itemDto : request.getItems()) {
            Item item = itemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemDto.getItemId()));

            PurchaseOrderItem poItem = new PurchaseOrderItem();
            poItem.setPurchaseOrder(po);
            poItem.setItem(item);
            poItem.setQuantity(itemDto.getQuantity());
            poItem.setPrice(itemDto.getPrice());
            
            items.add(poItem);
            totalAmount = totalAmount.add(itemDto.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }

        po.setItems(items);
        po.setTotalAmount(totalAmount);

        return purchaseOrderRepository.save(po);
    }

    @Transactional
    public PurchaseOrder approvePO(Long poId) {
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException("PO not found"));

        if (po.getStatus() != POStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT POs can be approved");
        }

        po.setStatus(POStatus.APPROVED);
        return purchaseOrderRepository.save(po);
    }
    
    public PurchaseOrder getPO(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PO not found"));
    }

    public List<PurchaseOrder> getAllOrders() {
        return purchaseOrderRepository.findAll();
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}
