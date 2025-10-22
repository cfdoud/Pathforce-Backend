package com.pathdx.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserNotificationSubscriptionResponseDto {

    private Long id;
    private Long userId;
    private Long notificationEventId;
    private String category;
    private String eventCode;
    private String description;
    private boolean isSelect;
}
