package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BulkUserConfigReqDto extends UserConfigReqDto{
    @JsonProperty
    List<UserConfigReqDto> userConfigReqDtoList;
}
