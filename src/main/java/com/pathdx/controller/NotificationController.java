package com.pathdx.controller;

import com.pathdx.dto.requestDto.NotificationEventDto;
import com.pathdx.dto.responseDto.CountUserNotification;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.dto.responseDto.UserNotificationResponseDto;
import com.pathdx.dto.responseDto.UserNotificationSubscriptionResponseDto;
import com.pathdx.model.UserNotificationModel;
import com.pathdx.model.UserNotificationSubscriptionModel;
import com.pathdx.service.UserNotificationService;
import com.pathdx.service.UserNotificationSubscriptionService;
import com.pathdx.utils.JwtTokenUtil;
import com.pathdx.utils.NotificationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notification")
@Slf4j
public class NotificationController {

    @Autowired
    private UserNotificationSubscriptionService userNotificationSubscriptionService;
    @Autowired
    private UserNotificationService userNotificationService;

    @Autowired
    private NotificationUtil notificationUtil;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @PostMapping("/save")
    public ResponseEntity<ResponseDto> saveNotificationAlert(@RequestBody NotificationEventDto notificationDto) {
        Long userId = notificationUtil.getUserIdByEmail(notificationDto.getEmailId());
        ResponseDto<List<UserNotificationSubscriptionResponseDto>> modelDto = userNotificationSubscriptionService.saveNotification(userId, notificationDto);
        return  ResponseEntity.ok(modelDto);
    }

    @GetMapping("/getNotificationByRole/{roleId}")
    public ResponseEntity<ResponseDto> getNotificationAlert(@RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headers,
                                                            @PathVariable("roleId") Long roleId) {
        String email= jwtTokenUtil.retrieveUserNameFromToken(headers);
        Long userId = notificationUtil.getUserIdByEmail(email);
        ResponseDto<List<UserNotificationSubscriptionResponseDto>> modelDto = userNotificationSubscriptionService.getUserNotificationSubscription(userId, roleId);
        return  ResponseEntity.ok(modelDto);
    }

    @GetMapping("/user/allNotification")
    public ResponseEntity<ResponseDto> getUserNotification(@RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headers){
        String email= jwtTokenUtil.retrieveUserNameFromToken(headers);
        Long userId = notificationUtil.getUserIdByEmail(email);
        ResponseDto<List<UserNotificationResponseDto>> modelDto = userNotificationService.getUserNotification(userId);
        return  ResponseEntity.ok(modelDto);
    }

    @GetMapping("/count")
    public ResponseEntity<ResponseDto>  getNotificationCount(@RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headers){
        ResponseDto<CountUserNotification> notificationCount = new ResponseDto<>();
        String email= jwtTokenUtil.retrieveUserNameFromToken(headers);
        Long userId = notificationUtil.getUserIdByEmail(email);

        notificationCount = userNotificationService.getNotificationCount(userId);
        return  ResponseEntity.ok(notificationCount);

    }

    @PutMapping("/{userNotificationId}")
    public ResponseEntity<ResponseDto> updateUserNotification(@PathVariable("userNotificationId") Long userNotificationId) {
        ResponseDto<List<UserNotificationResponseDto>> modelDto = userNotificationService.updateUserNotification(userNotificationId);
        return  ResponseEntity.ok(modelDto);
    }
}
