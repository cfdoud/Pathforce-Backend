package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pathdx.model.CaseAllocationConfigModel;
import com.pathdx.model.DesignationModel;
import com.pathdx.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseAllocationResponseDto {
    Long user_id;
    String firstName;
    String lastName;
    String email;
   // List<Role> roles;
  //  List<DesignationModel> Designation;
    String role;
    String designation;
    int maxPendingDays;
    int maxNumberOfCases;
}
