package com.pathdx.service.impl;

import com.pathdx.dto.requestDto.ActivityLogDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.service.ActivityLogService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("demo")
@Service
public class DemoActivityLogService implements ActivityLogService {

    @Override
    public ResponseDto getActivityLog(ActivityLogDto dto) {
        return new ResponseDto(true, "Demo Response", null);
    }
}


