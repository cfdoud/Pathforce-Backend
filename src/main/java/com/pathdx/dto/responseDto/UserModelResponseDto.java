package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserModelResponseDto {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String npi;
    private String streetAddress;
    private String searchAddress;
    private String city;
    private String homeState;
    private String zip;
    private String mobilePh;
    private String homePh;
    private String emergencyPh;
    private String email;
    private List LicensedStates ;
    private String degree;
    private String college;
    private String yearOfPassing;
    private String associatedLab;
    private String profileImg;
    private boolean isPasswordChangeRequired;
    private List<LabResponseDto> labDetails;
    private boolean isActive;
    private List<DesignationResponseDto> designation;
    private List<RoleResponseDto> roles;
    private Date lastLogin;
}
