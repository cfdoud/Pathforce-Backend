package com.pathdx.service;

import com.pathdx.dto.requestDto.LabHeadingsReqDto;
import com.pathdx.dto.responseDto.LabHeadingsResponseDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.model.LabHeadings;
import org.springframework.stereotype.Service;

@Service
public interface LabHeadingService {
    ResponseDto<LabHeadingsResponseDto> getLabHeadingDetails(String labId) throws Exception;

    ResponseDto<LabHeadingsResponseDto> updateLabHeadings(LabHeadingsReqDto labHeadingsReqDto) throws Exception;
}
