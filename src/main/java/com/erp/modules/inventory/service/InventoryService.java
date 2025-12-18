package com.erp.modules.inventory.service;

import com.erp.modules.inventory.dto.GoodsReceiptItemDto;
import com.erp.modules.inventory.dto.GoodsReceiptRequest;
import com.erp.modules.inventory.entity.*;
import com.erp.modules.inventory.repository.InventoryTransactionRepository;
import com.erp.modules.inventory.repository.StockRepository;
import com.erp.modules.inventory.repository.WarehouseRepository;
import com.erp.modules.procurement.entity.Item;
import com.erp.modules.procurement.entity.POStatus;
import com.erp.modules.procurement.entity.PurchaseOrder;
import com.erp.modules.procurement.repository.ItemRepository;
import com.erp.modules.procurement.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final StockRepository stockRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final WarehouseRepository warehouseRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public void processGoodsReceipt(GoodsReceiptRequest request) {
        PurchaseOrder po = purchaseOrderRepository.findById(request.getPurchaseOrderId())
                .orElseThrow(() -> new RuntimeException("PO not found"));

        if (po.getStatus() != POStatus.APPROVED && po.getStatus() != POStatus.PARTIALLY_RECEIVED) {
            throw new RuntimeException("PO must be APPROVED or PARTIALLY_RECEIVED to receive goods");
        }

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        for (GoodsReceiptItemDto itemDto : request.getItems()) {
            Item item = itemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            // 1. Create Transaction
            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setItem(item);
            transaction.setWarehouse(warehouse);
            transaction.setType(TransactionType.IN);
            transaction.setQuantity(itemDto.getQuantity());
            transaction.setReferenceId(po.getId());
            transaction.setReferenceType("PO");
            transactionRepository.save(transaction);

            // 2. Update Stock
            Stock stock = stockRepository.findByItemAndWarehouse(item, warehouse)
                    .orElse(new Stock(item, warehouse, 0, null));
            
            stock.setOnHand(stock.getOnHand() + itemDto.getQuantity());
            stockRepository.save(stock);
        }

        // Update PO Status (Simplified logic: assume fully received if any receipt happens for now, or keep PARTIALLY_RECEIVED)
        // In a real system, we would check if all items are fully received.
        po.setStatus(POStatus.PARTIALLY_RECEIVED); // Or CLOSED
        purchaseOrderRepository.save(po);
    }

    public java.util.List<Stock> getAllStock() {
        return stockRepository.findAll();
    }
    public List<PurchaseOrder> getPendingPurchaseOrders() {
        return purchaseOrderRepository.findByStatusIn(java.util.List.of(POStatus.APPROVED, POStatus.PARTIALLY_RECEIVED));
    }

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }}
