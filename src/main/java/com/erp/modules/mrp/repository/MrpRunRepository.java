package com.erp.modules.mrp.repository;

import com.erp.modules.mrp.entity.MrpRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MrpRunRepository extends JpaRepository<MrpRun, Long> {
}
