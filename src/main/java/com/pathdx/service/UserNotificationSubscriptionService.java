package com.pathdx.service;

import com.pathdx.dto.requestDto.NotificationEventDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.dto.responseDto.UserNotificationSubscriptionResponseDto;
import com.pathdx.model.UserNotificationSubscriptionModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserNotificationSubscriptionService {
    public ResponseDto<List<UserNotificationSubscriptionResponseDto>> saveNotification(Long userId, NotificationEventDto notificationEventDto);
    public ResponseDto<List<UserNotificationSubscriptionResponseDto>> getUserNotificationSubscription(Long userId, Long roleId);
}
