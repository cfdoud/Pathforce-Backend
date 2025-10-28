package com.pathdx.service.impl;

import com.pathdx.dto.requestDto.ActivityLogDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.service.ActivityLogService;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Profile("local")
@Primary
@Service
public class LocalActivityLogService implements ActivityLogService {

    @Override
    public ResponseDto getActivityLog(ActivityLogDto activityLogDTO) {
        ResponseDto dto = new ResponseDto(true, "Stubbed OK", HttpStatus.OK.name());
        dto.setStatusCode(HttpStatus.OK.value());
        dto.setResponse(Collections.emptyList());
        return dto;
    }
}
