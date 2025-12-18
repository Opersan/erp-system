package com.erp.modules.manufacturing.entity;

import com.erp.modules.common.BaseEntity;
import com.erp.modules.procurement.entity.Item;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "boms")
public class BOM extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item; // Finished Good

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "base_quantity", nullable = false)
    private BigDecimal baseQuantity = BigDecimal.ONE;

    @OneToMany(mappedBy = "bom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BOMComponent> components = new ArrayList<>();
}
