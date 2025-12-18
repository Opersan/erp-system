package com.erp.modules.procurement.repository;

import com.erp.modules.procurement.entity.POStatus;
import com.erp.modules.procurement.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByStatusIn(List<POStatus> statuses);
}
