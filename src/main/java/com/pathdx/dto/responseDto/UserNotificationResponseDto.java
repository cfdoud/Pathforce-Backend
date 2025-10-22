package com.pathdx.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Setter
@Getter
public class UserNotificationResponseDto {
    private Long id;
    private Long userId;
    private Long notificationEventId;
    private String message;
    private Date createDate;
    private boolean viewed;
}
