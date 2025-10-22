package com.pathdx.repository;

import com.pathdx.dto.responseDto.AllocationAssignedCasesOpenDto;
import com.pathdx.dto.responseDto.AllocationUnassignedOpenDto;
import com.pathdx.model.CaseAllocationConfigModel;
import com.pathdx.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CaseAllocationRepository extends JpaRepository<CaseAllocationConfigModel, Long> {
    Optional<CaseAllocationConfigModel> findByUserModel(UserModel userModel);

    List<CaseAllocationConfigModel> findAllByUserModelIn(List<UserModel> userModel);
    @Query(name="get Assinged cases info",value = "Select main.assingedCasesCount as AssingedCasesCount,main.user_Id as UserId, \n" +
            "ca.MaxNumberOfCases as MaxNumberOfCases,ca.MaxPendingDays as MaxPendingDays,\n" +
            "ca.Id as CaseAllocationId from (select count(*) as assingedCasesCount,u.Id as user_Id\n" +
            "from user_info u\n" +
            "join user_order_messages uo where u.Id=uo.user_id  group by user_id) main\n" +
            "join Case_Allocation_Settings ca on ca.user_id=main.user_Id \n" +
            "join pf_user_lab_detail pf where pf.user_id=main.user_id and pf.lab_id=?1\n" , nativeQuery = true)
    List<AllocationAssignedCasesOpenDto> findAssignedCasesDetails(String lab);

    @Query(name="get Assinged cases info",value = "SELECT \n" +
            " o.AccessionID AS AccessionId, o.Id AS OrderMessageId\n" +
            " FROM PF_OrderMessages o WHERE NOT EXISTS( SELECT  1\n" +
            " FROM user_order_messages u WHERE u.order_message_id = o.Id)\n" +
            " AND o.LabID = ?1\n" , nativeQuery = true)
    List<AllocationUnassignedOpenDto> findUnAssignedCasesAllocation(String labDetail);

}
