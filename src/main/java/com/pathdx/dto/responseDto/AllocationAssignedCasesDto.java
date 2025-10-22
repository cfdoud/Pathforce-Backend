package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllocationAssignedCasesDto {
    int AssingedCasesCount;
    int MaxNumberOfCases;
    Long UserId;
    int MaxPendingDays;
    Long CaseAllocationId;
}
