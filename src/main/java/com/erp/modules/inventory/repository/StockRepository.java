package com.erp.modules.inventory.repository;

import com.erp.modules.inventory.entity.Stock;
import com.erp.modules.inventory.entity.Warehouse;
import com.erp.modules.procurement.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByItemAndWarehouse(Item item, Warehouse warehouse);
}
