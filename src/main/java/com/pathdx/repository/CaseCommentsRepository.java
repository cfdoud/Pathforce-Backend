package com.pathdx.repository;

import com.pathdx.model.CaseComments;
import com.pathdx.model.CaseDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseCommentsRepository  extends JpaRepository<CaseComments,Long> {

    List<CaseComments> findByCaseDetails(CaseDetails caseDetails);

}
