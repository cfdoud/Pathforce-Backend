package com.pathdx.utils;

import com.pathdx.constant.ActionType;
import com.pathdx.dto.requestDto.UserNotificationDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.model.*;
import com.pathdx.repository.NotificationRepository;
import com.pathdx.repository.UserNotificationRepository;
import com.pathdx.repository.UserNotificationSubscriptionRepository;
import com.pathdx.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class NotificationUtil {

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private UserNotificationSubscriptionRepository userNotificationSubscriptionRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UsersRepository usersRepository;

    public List<UserNotificationModel> saveUserNotification(ActionType actionType, String email, String labName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateObj = null;
        List<NotificationEventModel> listOfNotification = getNotificationEvents(actionType);
        List<UserNotificationModel> lstOfUserNotification = new ArrayList<UserNotificationModel>();
        if (!listOfNotification.isEmpty()) {
            for (NotificationEventModel notificationEventModel : listOfNotification) {
                List<UserNotificationSubscriptionModel> lstUserNotificationSubscription = userNotificationSubscriptionRepository.findByNotificationEventId(notificationEventModel.getId());
                for (UserNotificationSubscriptionModel userNotificationSubscriptionModel : lstUserNotificationSubscription) {
                    if (userNotificationSubscriptionModel.isSelect()) {
                        String message = notificationEventModel.getMessage();
                        Object[] args = {email, labName};
                        String message1 = getMessageFormat(message, args);
                        UserNotificationModel userNotificationModel = new UserNotificationModel();
                        userNotificationModel.setUserId(userNotificationSubscriptionModel.getUserId());
                        userNotificationModel.setNotificationEventId(notificationEventModel.getId());
                        userNotificationModel.setMessage(message1);
                        userNotificationModel.setViewed(false);
                        try {
                            dateObj = sdf.parse(sdf.format(new Date()));
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                        }

                        userNotificationModel.setCreateDate(dateObj);
                        lstOfUserNotification.add(userNotificationModel);
                    }
                }
            }
        }

        List<UserNotificationModel> userNotificationObj = userNotificationRepository.saveAll(lstOfUserNotification);
        return userNotificationObj;
    }

    public List<UserNotificationModel> saveUserNotification (ActionType actionType, String caseId, String emailId,String labName){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateObj = null;
        List<NotificationEventModel> listOfNotification = getNotificationEvents(actionType);
        List<UserNotificationModel> lstOfUserNotification = new ArrayList<UserNotificationModel>();
        if (!listOfNotification.isEmpty()) {
            for (NotificationEventModel notificationEventModel : listOfNotification) {
                List<UserNotificationSubscriptionModel> lstUserNotificationSubscription = userNotificationSubscriptionRepository.findByNotificationEventId(notificationEventModel.getId());
                for (UserNotificationSubscriptionModel userNotificationSubscriptionModel : lstUserNotificationSubscription) {
                    if (userNotificationSubscriptionModel.isSelect()) {
                        String message = notificationEventModel.getMessage();
                        Object[] args = null;
                        if(actionType.equals(ActionType.CASE_ASSIGNED_BY_ADMIN)) {
                            args = new Object[]{caseId, emailId, labName};
                        }else{
                            args = new Object[]{caseId, labName};
                        }
                        String message1 = getMessageFormat(message, args);
                        UserNotificationModel userNotificationModel = new UserNotificationModel();
                        userNotificationModel.setUserId(userNotificationSubscriptionModel.getUserId());
                        userNotificationModel.setNotificationEventId(notificationEventModel.getId());
                        userNotificationModel.setMessage(message1);
                        userNotificationModel.setViewed(false);
                        try {
                            dateObj = sdf.parse(sdf.format(new Date()));
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                        }

                        userNotificationModel.setCreateDate(dateObj);
                        lstOfUserNotification.add(userNotificationModel);
                    }
                }
            }
        }

        List<UserNotificationModel> userNotificationObj = userNotificationRepository.saveAll(lstOfUserNotification);
        return userNotificationObj;
    }


    private String getMessageFormat(String message, Object[] args) {
        MessageFormat messageFormat = new MessageFormat(message);
        String result = messageFormat.format(args);
        return result;
    }

    public List<NotificationEventModel> getNotificationEvents(ActionType action) {
        List<String> eventCodes = new ArrayList<String>();
        switch (action) {
            case NEW_LAB_REVIEWER_REGISTERED:
                eventCodes = Arrays.asList("NEW_USER_REGISTERED_WITH_MYLAB", "NEW_USER_REGISTERED_WITH_ANYLAB");
                break;
            case CASE_ASSIGNED_BY_RULE:
                eventCodes = Arrays.asList("CASE_ASSIGNED_BY_RULE");
                break;

            case CASE_ASSIGNED_BY_ADMIN:
                eventCodes = Arrays.asList("CASE_ASSIGNED_BY_ADMIN");
                break;

            case NEW_ADMIN_REGISTERED:
                eventCodes = Arrays.asList("NEW_ADMIN_USER_REGISTERED_WITH_MYLAB", "NEW_ADMIN_USER_REGISTERED_WITH_ANYLAB");
                break;

            case USER_DEACTIVATED:
                eventCodes = Arrays.asList("USER_DEACTIVATED_FROM_MYLAB", "USER_DEACTIVATED_FROM_ANYLAB");
                break;

        }
        List<NotificationEventModel> lstNotificationEvents = new ArrayList<NotificationEventModel>();
        if(lstNotificationEvents.isEmpty()) {
            for (String eventCode : eventCodes) {
                NotificationEventModel notificationEventModel = notificationRepository.findByEventCode(eventCode);
                lstNotificationEvents.add(notificationEventModel);
            }
        }
        return  lstNotificationEvents;
    }
    public Long getUserIdByEmail(String emailId) {
        UserModel userModel = usersRepository.findUserModelByEmail(emailId).get();
        return userModel.getId();
    }

    public List<Role> getRoleIdsByEmail(String emailId) {
        UserModel userModel = usersRepository.findUserModelByEmail(emailId).get();
        return userModel.getRoles();
    }
}
