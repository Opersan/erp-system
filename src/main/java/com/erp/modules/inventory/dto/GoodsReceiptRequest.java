package com.erp.modules.inventory.dto;

import lombok.Data;

import java.util.List;

@Data
public class GoodsReceiptRequest {
    private Long purchaseOrderId;
    private Long warehouseId;
    private List<GoodsReceiptItemDto> items;
}
