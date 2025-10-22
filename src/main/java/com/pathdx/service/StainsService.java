package com.pathdx.service;

import com.pathdx.dto.requestDto.SlideDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.dto.responseDto.SlideResDto;
import com.pathdx.dto.responseDto.StainsDto;
import com.pathdx.dto.responseDto.StainsResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StainsService {
    public ResponseDto<StainsResponseDto> getAllStains();

    public ResponseDto<SlideResDto> saveStainsAndStainPanel(SlideDto slideDto, ResponseDto<SlideResDto> slideResDto, String email, String accessionId);
}
