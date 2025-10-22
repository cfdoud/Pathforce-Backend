package com.pathdx.controller;

import com.pathdx.dto.responseDto.NotificationRoleDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.service.NotificationEventApplicableRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.pathdx.constant.Constants.SUCCESS_MESSAGE;

@RestController
@RequestMapping("/role")
@Slf4j
public class NotificationEventApplicableRoleController {


    @Autowired
    private NotificationEventApplicableRoleService notificationEventApplicableRoleService;

    @GetMapping("/notification/{roleId}")
    public ResponseEntity<ResponseDto> getNotificationsByRoleId(@PathVariable("roleId") Long roleId) {
        NotificationRoleDto notificationEvent = notificationEventApplicableRoleService.getNotificationByRoleId(roleId);
        return  ResponseEntity.ok(new ResponseDto(notificationEvent,"", SUCCESS_MESSAGE , HttpStatus.OK.value()));
    }
}
