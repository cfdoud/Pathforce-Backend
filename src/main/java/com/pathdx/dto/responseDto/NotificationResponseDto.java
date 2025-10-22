package com.pathdx.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationResponseDto {

    private Long notificationId;
    private String category;
    private String eventCode;
    private String description;
    private String message;



}
