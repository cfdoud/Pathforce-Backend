package com.pathdx.repository;

import com.pathdx.model.CaseImageAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaseImageAuditRepository extends JpaRepository<CaseImageAudit, Long> {
//    Optional<CaseImageAudit> findFirstByTileGenerated(boolean tileGenerated);
    Optional<CaseImageAudit> findCaseImageAuditByBarcodeId(String barCode);
    Optional<CaseImageAudit> findCaseImageAuditByBarcodeIdAndLabId(String barCode,String labid);
}

