package com.pathdx.service;

import com.pathdx.dto.responseDto.CountUserNotification;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.dto.responseDto.UserNotificationResponseDto;
import com.pathdx.model.UserNotificationModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserNotificationService {
    public ResponseDto<List<UserNotificationResponseDto>> getUserNotification(Long userId);


    public ResponseDto<CountUserNotification> getNotificationCount(Long userId);

    public ResponseDto<List<UserNotificationResponseDto>> updateUserNotification(Long userNotificationId);
}
