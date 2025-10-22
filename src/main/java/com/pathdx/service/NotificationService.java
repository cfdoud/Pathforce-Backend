package com.pathdx.service;

import com.pathdx.model.NotificationEventModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {
    public List<NotificationEventModel> getAllNotification();
    public NotificationEventModel getNotificationByEventCode(String eventCode);

    public NotificationEventModel getNotificationByNotificationId(Long id);

    public List<NotificationEventModel> getNotificationsByNotificationIds(List<Long> ids);
}
