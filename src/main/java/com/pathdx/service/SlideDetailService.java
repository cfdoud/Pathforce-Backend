package com.pathdx.service;

import com.pathdx.dto.requestDto.SlideDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.dto.responseDto.SlideDetailsDto;
import com.pathdx.dto.responseDto.SlideResDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SlideDetailService {
    public ResponseDto<List<SlideDetailsDto>> getSlideDetails(Long caseDetailId);

    public ResponseDto<SlideResDto> saveSlideDetails(SlideDto slideDto);

    ResponseDto getData(String barCode, String labid, String accessionId, String caseId);
}
