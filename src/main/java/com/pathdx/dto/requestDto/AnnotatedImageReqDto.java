package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AnnotatedImageReqDto {
    @JsonProperty
    private String email;

    @JsonProperty
    private String accessionId;

    @JsonProperty
    private String caseId;

    @JsonProperty
    private String barCodeId;

    @JsonProperty
    private String filePath;

    @JsonProperty
    private String base64URL;
}
