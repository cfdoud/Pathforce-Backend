package com.pathdx.service;

import com.pathdx.dto.requestDto.UserConfigReqDto;
import com.pathdx.dto.responseDto.CaseAllocationResponseDto;
import com.pathdx.dto.responseDto.CountAssignedAndUnAssigned;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.model.CaseAllocationConfigModel;
import com.pathdx.model.LabDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CaseAllocationService {

    ResponseDto<List<CaseAllocationResponseDto>> getAllLabUsers(LabDetail labDetail) throws Exception;
    ResponseDto updateUserConfig(UserConfigReqDto userConfigReqDto,String email) throws Exception;

    ResponseDto updateBulkUserConfig(List<UserConfigReqDto> userConfigReqDtoList) throws Exception;

    ResponseDto<CountAssignedAndUnAssigned> getAssignedCasesCount(String labId);

    void casesAllocation();

    void sendEmialForPendingCases();

}
