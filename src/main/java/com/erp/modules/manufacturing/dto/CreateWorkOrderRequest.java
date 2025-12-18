package com.erp.modules.manufacturing.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateWorkOrderRequest {
    private Long itemId;
    private BigDecimal quantity;
    private String startDate; // ISO Date
    private String endDate; // ISO Date
}
