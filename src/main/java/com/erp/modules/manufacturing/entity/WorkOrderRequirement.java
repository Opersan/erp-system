package com.erp.modules.manufacturing.entity;

import com.erp.modules.common.BaseEntity;
import com.erp.modules.procurement.entity.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "work_order_requirements")
public class WorkOrderRequirement extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "work_order_id", nullable = false)
    @JsonIgnore
    private WorkOrder workOrder;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "required_quantity", nullable = false)
    private BigDecimal requiredQuantity;

    @Column(name = "allocated_quantity")
    private BigDecimal allocatedQuantity = BigDecimal.ZERO;

    @Column(name = "issued_quantity")
    private BigDecimal issuedQuantity = BigDecimal.ZERO;
}
