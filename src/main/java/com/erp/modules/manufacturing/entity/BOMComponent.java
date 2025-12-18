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
@Table(name = "bom_components")
public class BOMComponent extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "bom_id", nullable = false)
    @JsonIgnore
    private BOM bom;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item; // Component

    @Column(nullable = false)
    private BigDecimal quantity;
}
