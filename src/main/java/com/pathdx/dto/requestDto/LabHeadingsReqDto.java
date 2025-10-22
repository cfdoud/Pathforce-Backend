package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LabHeadingsReqDto {
    @JsonProperty
    private String id;

    @JsonProperty
    private String email;
    @JsonProperty
    private String firstHeading;
    @JsonProperty
    private String secondHeading;
    @JsonProperty
    private String thirdHeading;
    @JsonProperty
    private String fourthHeading;
    @JsonProperty
    private String fifthHeading;
    @JsonProperty
    private String sixthHeading;
    @JsonProperty
    private String seventhHeading;
    @JsonProperty
    private String labId;
}
