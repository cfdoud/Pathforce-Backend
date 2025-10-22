package com.pathdx.service.impl;

import com.pathdx.model.NotificationEventModel;
import com.pathdx.repository.NotificationRepository;
import com.pathdx.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<NotificationEventModel> getAllNotification() {
        return notificationRepository.findAll();
    }

    public NotificationEventModel getNotificationByEventCode(String eventCode) {
        return notificationRepository.findByEventCode(eventCode);
    }

    @Override
    public NotificationEventModel getNotificationByNotificationId(Long id) {
        Optional<NotificationEventModel> notificationEventModel = notificationRepository.findById(id);
        return notificationEventModel.get();
    }

    @Override
    public List<NotificationEventModel> getNotificationsByNotificationIds(List<Long> ids) {
        List<NotificationEventModel> notificationEventsModel = notificationRepository.findAllById(ids);
        return notificationEventsModel;
    }
}
