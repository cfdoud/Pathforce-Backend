package com.pathdx.service;

import com.pathdx.dto.requestDto.ActivityLogDto;
import com.pathdx.dto.responseDto.ResponseDto;

public interface ActivityLogService {
    ResponseDto getActivityLog(ActivityLogDto activityLogDTO);
}
