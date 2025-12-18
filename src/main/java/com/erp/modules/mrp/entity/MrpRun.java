package com.erp.modules.mrp.entity;

import com.erp.modules.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mrp_runs")
public class MrpRun extends BaseEntity {

    @Column(name = "run_date", nullable = false)
    private LocalDateTime runDate;

    @Column(name = "horizon_days", nullable = false)
    private Integer horizonDays;
}
