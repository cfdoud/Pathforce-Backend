package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseToUserDto {
    @JsonProperty
    private String labId;

    @JsonProperty
    private Long orderMessageId;

    @JsonProperty
    private String userMail;
}
