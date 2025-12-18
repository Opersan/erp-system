package com.erp.modules.manufacturing.repository;

import com.erp.modules.manufacturing.entity.BOM;
import com.erp.modules.procurement.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BOMRepository extends JpaRepository<BOM, Long> {
    Optional<BOM> findByItem(Item item);
}
