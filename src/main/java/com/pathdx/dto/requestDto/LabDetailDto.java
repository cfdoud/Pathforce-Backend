package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class LabDetailDto implements Serializable {

        @JsonProperty
        private String labId;

        @JsonProperty
        private String labName;

        @JsonProperty
        private String LabContactNo;

        @JsonProperty
        private String labEmail;

        @JsonProperty
        private String labWebsite;

}
