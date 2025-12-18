package com.erp.modules.procurement.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreatePORequest {
    private Long supplierId;
    private List<PurchaseOrderItemDto> items;
}
