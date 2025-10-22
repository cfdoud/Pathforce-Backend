package com.pathdx.repository;

import com.pathdx.model.LabDetail;
import com.pathdx.model.OrderMessages;
import com.pathdx.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface OrderMessagesRepository extends JpaRepository<OrderMessages,Long> {


    @Query("SELECT o FROM OrderMessages o INNER JOIN o.userModels u INNER JOIN o.labDetail WHERE u IN (:userModel) and " +
            "o.labDetail in (:labDetail) and o.caseStatus in (:status, :fStatus)")
    Page<OrderMessages> findByUserModels(@Param("userModel") Set<UserModel> userModel, @Param("status") String status,
                                         @Param("fStatus") String fStatus,@Param("labDetail") LabDetail labDetail, Pageable pageable);

    OrderMessages findByAccessionIdAndLabDetail(String accessionId, LabDetail labDetail);

    @Query("SELECT o FROM OrderMessages o INNER JOIN o.labDetail l WHERE l IN (:labDetail) and o.caseStatus " +
            "in (:status, :fStatus)")
    Page<OrderMessages> findByLabDetailAndCaseStatus(@Param("labDetail") LabDetail labDetail,
                                                     @Param("status") String status,@Param("fStatus") String fStatus,
                                                     Pageable paging);

    @Query("SELECT o FROM OrderMessages o INNER JOIN o.labDetail l WHERE l IN (:labDetail) and o.caseStatus not in " +
            "(:status, :fStatus)")
    Page<OrderMessages> findByCaseStatusAndLabDetail(@Param("status") String status,@Param("fStatus") String fStatus,
                                                     @Param("labDetail") LabDetail labDetail, Pageable paging);

    @Query("SELECT o FROM OrderMessages o INNER JOIN o.userModels u INNER JOIN o.labDetail WHERE u IN (:userModel) and " +
            "o.labDetail in (:labDetail) and o.caseStatus not in (:status, :fStatus)")
    Page<OrderMessages> findByUserModelsAndLabDetail(@Param("userModel") Set<UserModel> userModel,@Param("status") String status,
                                                     @Param("fStatus")  String fStatus,
                                                     @Param("labDetail") LabDetail labDetail, Pageable paging);

    //This method is using for case allocation
    List<OrderMessages> findByLabDetailAndCaseStatus(LabDetail labDetail, String CaseStatus);

    OrderMessages findByAccessionId(String accessionId);
}