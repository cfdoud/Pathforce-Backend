package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class LabDetailsRequestDto {
    @JsonProperty
    private String labId;

    @JsonProperty
    private String labName;

    @JsonProperty
    private String userName;

   /* @JsonProperty
    private Date dateCreated;*/

    /*@JsonProperty
    private Date lastModifiedDate;*/

    /*@JsonProperty
    private String createdBy;*/

    @JsonProperty
    private String lastModifiedBy;

    @JsonProperty
    private String labEmail;

    @JsonProperty
    private String labContactNo;

    @JsonProperty
    private String labRegistrationDocument;

    @JsonProperty
    private String labRegistrationNo;

    @JsonProperty
    private String labWebsite;

    @JsonProperty
    private String street;

    @JsonProperty
    private String city;

    @JsonProperty
    private String state;

    @JsonProperty
    private Long zip;
}
