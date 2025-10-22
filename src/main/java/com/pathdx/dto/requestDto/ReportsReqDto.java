package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ReportsReqDto {

    @JsonProperty
    private String email;

    @JsonProperty
    private String labId;

    @JsonProperty
    private Long orderMessageId;

    @JsonProperty
    private String caseId;

    @JsonProperty
    private List<String> attachedImages;

    @JsonProperty
    private String comments;
}
