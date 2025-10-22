package com.pathdx.service;

import com.pathdx.dto.requestDto.LabDetailDto;
import com.pathdx.dto.requestDto.LabDetailsRequestDto;
import com.pathdx.dto.responseDto.LabResponseDto;
import com.pathdx.dto.responseDto.MasterResponseDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.model.LabDetail;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface LabDetailService {

    ResponseDto<List<MasterResponseDto>> getAllLabs() throws Exception;

    ResponseDto<List<LabResponseDto>> getAllLabsDetails() throws Exception;
    //ResponseDto<List<MasterResponseDto>> getLabDetails(String id);
    ResponseDto<LabResponseDto> getLabDetails(String id);

    ResponseDto<List<MasterResponseDto>> getAssociatedlabs(String retrieveUserNameFromToken);

    ResponseDto<LabResponseDto> saevLabDetails(LabDetailsRequestDto labDetailsRequestDto,String email) throws Exception;
}
