package com.pathdx.service.impl;

import com.pathdx.dto.responseDto.NotificationResponseDto;
import com.pathdx.dto.responseDto.NotificationRoleDto;
import com.pathdx.model.NotificationEventModel;
import com.pathdx.model.NotificationEventsApplicableRoleModel;
import com.pathdx.repository.NotificationEventApplicableRoleRepository;
import com.pathdx.service.NotificationEventApplicableRoleService;
import com.pathdx.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NotificationEventApplicableServiceImp implements NotificationEventApplicableRoleService {

    @Autowired
    private NotificationEventApplicableRoleRepository notificationEventApplicableRoleRepository;


    @Autowired
    private NotificationService notificationService;

    public NotificationRoleDto getNotificationByRoleId(Long roleId) {
        List<NotificationEventsApplicableRoleModel> lists = notificationEventApplicableRoleRepository.findByRoleId(roleId);
        List<NotificationEventModel> listOfNotification = new ArrayList<NotificationEventModel>();
        listOfNotification = lists.stream()
                .map(l-> notificationService.getNotificationByNotificationId(l.getNotificationEventId()))
                .collect(Collectors.toList());
        NotificationRoleDto notificationRoleDto = new NotificationRoleDto();
        notificationRoleDto.setRoleId(roleId);
        List<NotificationResponseDto> ListOfNotificationDto = convertModelToDto(listOfNotification);
        notificationRoleDto.setListOfNotification(ListOfNotificationDto);
        return notificationRoleDto;
    }

    private List<NotificationResponseDto> convertModelToDto(List<NotificationEventModel> notificationEvents) {
        List<NotificationResponseDto> ListOfNotificationDto = new ArrayList<NotificationResponseDto>();
        for(NotificationEventModel notificationEvent : notificationEvents) {
            NotificationResponseDto notificationResponse = new NotificationResponseDto();
            notificationResponse.setNotificationId(notificationEvent.getId());
            notificationResponse.setCategory(notificationEvent.getCategory());
            notificationResponse.setEventCode(notificationEvent.getEventCode());
            notificationResponse.setDescription(notificationEvent.getDescription());
            notificationResponse.setMessage(notificationEvent.getMessage());
            ListOfNotificationDto.add(notificationResponse);
        }
        return ListOfNotificationDto;
    }
}
