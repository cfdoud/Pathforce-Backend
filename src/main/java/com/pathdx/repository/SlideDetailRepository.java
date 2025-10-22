package com.pathdx.repository;

import com.pathdx.model.SlideDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlideDetailRepository extends JpaRepository<SlideDetails,Long> {
   /* @Query(value = "select count(*) from pathdx.PF_SlideDetails s join \n" +
            "pathdx.PF_CaseDetails c on s.CaseDetailId=c.Id where c.OrderMessageId = ?1", nativeQuery = true)
    int findByOrderMessageId(Long id);*/

    @Query(value = "select * from PF_SlideDetails s join \n" +
            " PF_CaseDetails c on s.CaseDetailId=c.Id where c.Id = ?1", nativeQuery = true)
    public List<SlideDetails> findByCaseDetailId(Long caseDetailId);

    SlideDetails findByBarCodeid(String barcodeId);
}
