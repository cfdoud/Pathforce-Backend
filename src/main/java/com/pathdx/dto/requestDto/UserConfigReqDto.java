package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserConfigReqDto {
    @JsonProperty
    String labId;

    @JsonProperty
    int maxNumberOfCases;

    @JsonProperty
    int maxPendingDays;

    @JsonProperty
    Long user_id;
}
