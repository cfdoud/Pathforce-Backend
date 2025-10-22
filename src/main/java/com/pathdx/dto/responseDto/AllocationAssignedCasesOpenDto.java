package com.pathdx.dto.responseDto;

public interface AllocationAssignedCasesOpenDto {
    Long getCaseAllocationId();
    int getAssingedCasesCount();
    int getMaxNumberOfCases();
    Long getUserId();
    int getMaxPendingDays();

}
