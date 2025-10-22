package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class NotificationEventDto {
    @JsonProperty
    private String emailId;

    @JsonProperty
    private List<NotificationReqDto> notifications;
}
