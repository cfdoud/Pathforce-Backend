package com.pathdx.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NotificationRoleDto {
    private Long roleId;
    private List<NotificationResponseDto> listOfNotification;
}
