package com.pathdx.service;

import com.pathdx.dto.requestDto.ActivityLogDto;
import com.pathdx.dto.responseDto.ResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface ActivityLogService {

    public ResponseDto getActivityLog(ActivityLogDto activityLogDTO);
}
