package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseDetailsDto implements Serializable {

        @JsonProperty
        private Long id;

        @JsonProperty
        private Long orderMessageId;

        @JsonProperty
        private String caseId;

        @JsonProperty
        private boolean reportGenerated;


}
