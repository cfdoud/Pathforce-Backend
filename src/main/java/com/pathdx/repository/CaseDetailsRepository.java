package com.pathdx.repository;

import com.pathdx.model.CaseDetails;
import com.pathdx.model.OrderMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface CaseDetailsRepository extends JpaRepository<CaseDetails,Long> {

    CaseDetails getById(long id);


    @Query(value = "select * from PF_CaseDetails c WHERE \n" +
            " c.CaseId = ?1 ORDER BY Id DESC limit 1", nativeQuery = true)
    CaseDetails findByCaseId(String caseId);


    Set<CaseDetails> findByOrderMessages(OrderMessages orderMessages);
}
