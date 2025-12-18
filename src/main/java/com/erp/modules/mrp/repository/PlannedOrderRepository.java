package com.erp.modules.mrp.repository;

import com.erp.modules.mrp.entity.MrpRun;
import com.erp.modules.mrp.entity.PlannedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlannedOrderRepository extends JpaRepository<PlannedOrder, Long> {
    List<PlannedOrder> findByMrpRun(MrpRun mrpRun);
}
