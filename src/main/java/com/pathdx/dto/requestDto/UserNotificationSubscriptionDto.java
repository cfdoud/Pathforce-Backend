package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserNotificationSubscriptionDto {
    @JsonProperty
    private Long userId;

    @JsonProperty
    private List<Long> notificationEventId;
}
