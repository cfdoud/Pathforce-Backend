package com.pathdx.service;

import com.pathdx.dto.responseDto.NotificationRoleDto;
import org.springframework.stereotype.Service;

@Service
public interface NotificationEventApplicableRoleService {
    public NotificationRoleDto getNotificationByRoleId(Long roleId);
}
