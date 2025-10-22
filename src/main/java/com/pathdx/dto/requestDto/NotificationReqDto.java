package com.pathdx.dto.requestDto;

import lombok.Data;

@Data
public class NotificationReqDto {
    private Long id;
    public Long notificationEventId;
    public boolean isSelect;
}