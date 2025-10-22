package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.net.URL;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabResponseDto {
    private String labId;
    private String labName;
    private String userName;
    private Date dateCreated;
    private Date lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private String labEmail;
    private String labContactNo;
    private String labRegistrationDocument;
    private String labRegistrationNo;
    private String labWebsite;

    private URL registrationDocSignedUrl;
    private URL logoSignedUrl;

    private String street;
    private String city;
    private String state;
    private Long zip;

}
