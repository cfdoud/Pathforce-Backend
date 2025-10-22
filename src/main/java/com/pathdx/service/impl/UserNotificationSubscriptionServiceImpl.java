package com.pathdx.service.impl;

import com.pathdx.constant.AuditAction;
import com.pathdx.dto.requestDto.NotificationEventDto;
import com.pathdx.dto.requestDto.NotificationReqDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.dto.responseDto.UserNotificationResponseDto;
import com.pathdx.dto.responseDto.UserNotificationSubscriptionResponseDto;
import com.pathdx.model.ActionModel;
import com.pathdx.model.NotificationEventModel;
import com.pathdx.model.NotificationEventsApplicableRoleModel;
import com.pathdx.model.UserNotificationSubscriptionModel;
import com.pathdx.repository.NotificationEventApplicableRoleRepository;
import com.pathdx.repository.UserNotificationSubscriptionRepository;
import com.pathdx.service.NotificationService;
import com.pathdx.service.UserNotificationSubscriptionService;
import com.pathdx.utils.AuditLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserNotificationSubscriptionServiceImpl implements UserNotificationSubscriptionService {

    @Autowired
    private UserNotificationSubscriptionRepository userNotificationSubscriptionRepository;

    @Autowired
    private NotificationEventApplicableRoleRepository notificationEventApplicableRoleRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditLogUtil auditLogUtil;

    public ResponseDto<List<UserNotificationSubscriptionResponseDto>> saveNotification(Long userId, NotificationEventDto notificationEventDto) {
        ResponseDto<List<UserNotificationSubscriptionResponseDto>> response = new ResponseDto<>();
        List<UserNotificationSubscriptionModel> lstUserNotificationSubscription = new ArrayList<UserNotificationSubscriptionModel>();
        UserNotificationSubscriptionModel userNotificationSubscriptionModel1 = null;
        for (NotificationReqDto notification : notificationEventDto.getNotifications()) {
            UserNotificationSubscriptionModel userNotificationSubscriptionModel = new UserNotificationSubscriptionModel();
            if(notification.getId()!=null) {
                userNotificationSubscriptionModel.setId(notification.getId());
            }
            userNotificationSubscriptionModel.setUserId(userId);
            userNotificationSubscriptionModel.setNotificationEventId(notification.notificationEventId);
            userNotificationSubscriptionModel.setSelect(notification.isSelect());
            userNotificationSubscriptionModel1 = userNotificationSubscriptionRepository.save(userNotificationSubscriptionModel);
            lstUserNotificationSubscription.add(userNotificationSubscriptionModel1);
        }
        List<UserNotificationSubscriptionResponseDto> lstUserNotification = convertListModeltoDto(lstUserNotificationSubscription);

        //for Audit log
        ActionModel actionModel = auditLogUtil.getActions(AuditAction.SETTING_NOTIFICATION);
        Object[] args = {notificationEventDto.getEmailId()};
        String msg = auditLogUtil.getMessageFormat(actionModel.getDescription(),args);
        auditLogUtil.saveAuditLogs(AuditAction.SETTING_NOTIFICATION, userId, msg);

        response.setResponse(lstUserNotification);
        return  response;
    }

    @Override
    public ResponseDto<List<UserNotificationSubscriptionResponseDto>> getUserNotificationSubscription(Long userId, Long roleId) {
        ResponseDto<List<UserNotificationSubscriptionResponseDto>> response = new ResponseDto<>();
        List<UserNotificationSubscriptionModel> userNotificationSubscriptionModel =  userNotificationSubscriptionRepository.findByUserId(userId);
        if(userNotificationSubscriptionModel.isEmpty()) {
            List<NotificationEventsApplicableRoleModel> lists = notificationEventApplicableRoleRepository.findByRoleId(roleId);
            List<NotificationEventModel> listOfNotification = new ArrayList<NotificationEventModel>();
            listOfNotification = lists.stream()
                    .map(l-> notificationService.getNotificationByNotificationId(l.getNotificationEventId()))
                    .collect(Collectors.toList());
            List<UserNotificationSubscriptionResponseDto> listOfNotificationDto = convertModelToDto(listOfNotification, userId);
            response.setResponse(listOfNotificationDto);
            return response;
        }
        List<UserNotificationSubscriptionResponseDto> listOfNotificationDto = convertListModeltoDto(userNotificationSubscriptionModel);
        response.setResponse(listOfNotificationDto);
        return response;
    }

    private List<UserNotificationSubscriptionResponseDto> convertModelToDto(List<NotificationEventModel> notificationEvents, Long userId) {
        List<UserNotificationSubscriptionResponseDto> ListOfNotificationDto = new ArrayList<UserNotificationSubscriptionResponseDto>();
        for(NotificationEventModel notificationEvent : notificationEvents) {
            UserNotificationSubscriptionResponseDto notificationResponse = new UserNotificationSubscriptionResponseDto();
            notificationResponse.setNotificationEventId(notificationEvent.getId());
            notificationResponse.setUserId(userId);
            notificationResponse.setEventCode(notificationEvent.getEventCode());
            notificationResponse.setDescription(notificationEvent.getDescription());
            notificationResponse.setCategory(notificationEvent.getCategory());
            notificationResponse.setDescription(notificationEvent.getDescription());
            notificationResponse.setEventCode(notificationEvent.getEventCode());
            notificationResponse.setSelect(false);
            ListOfNotificationDto.add(notificationResponse);
        }
        return ListOfNotificationDto;
    }

    private List<UserNotificationSubscriptionResponseDto> convertListModeltoDto(List<UserNotificationSubscriptionModel> lstUserNotificationSubscriptionModel) {
        List<UserNotificationSubscriptionResponseDto> lstUserNotification = new ArrayList<UserNotificationSubscriptionResponseDto>();
        for(UserNotificationSubscriptionModel userNotificationSubscriptionModel : lstUserNotificationSubscriptionModel) {
            UserNotificationSubscriptionResponseDto userNotificationSubscriptionResponseDto = new UserNotificationSubscriptionResponseDto();
            userNotificationSubscriptionResponseDto.setId(userNotificationSubscriptionModel.getId());
            userNotificationSubscriptionResponseDto.setNotificationEventId(userNotificationSubscriptionModel.getNotificationEventId());
            NotificationEventModel notificationEventModel = notificationService.getNotificationByNotificationId(userNotificationSubscriptionModel.getNotificationEventId());
            userNotificationSubscriptionResponseDto.setCategory(notificationEventModel.getCategory());
            userNotificationSubscriptionResponseDto.setEventCode(notificationEventModel.getEventCode());
            userNotificationSubscriptionResponseDto.setDescription(notificationEventModel.getDescription());
            userNotificationSubscriptionResponseDto.setSelect(userNotificationSubscriptionModel.isSelect());
            userNotificationSubscriptionResponseDto.setUserId(userNotificationSubscriptionModel.getUserId());
            lstUserNotification.add(userNotificationSubscriptionResponseDto);
        }
        return lstUserNotification;
    }
}
