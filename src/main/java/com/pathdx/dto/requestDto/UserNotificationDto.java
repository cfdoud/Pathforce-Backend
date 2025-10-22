package com.pathdx.dto.requestDto;


import com.pathdx.constant.ActionType;
import lombok.Data;

import java.util.Optional;

@Data
public class UserNotificationDto {
    private Optional<Long> userId;
    private Optional<String> emailId;
    private Optional<String> labName;
    private ActionType action;

    private String accessionId;
}
