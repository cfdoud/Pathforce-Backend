package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
public class UserModelReqDto implements Serializable {

    @JsonProperty
    @NotBlank(message = "First Name is mandatory")
    private String firstName;

    @JsonProperty
    private String middleName;

    @JsonProperty
    @NotBlank(message = "Last Name is mandatory")
    private String lastName;

    @JsonProperty
    private String gender;

    @JsonProperty
    private String npi;

    @JsonProperty
    private String streetAddress;

    @JsonProperty
    private String searchAddress;

    @JsonProperty
    private String city;

    @JsonProperty
    private String homeState;

    @JsonProperty
    @NotBlank(message = "Zip is mandatory")
    private String zip;

    @JsonProperty
    private String mobilePh;

    @JsonProperty
    private String homePh;

    @JsonProperty
    private String emergencyPh;

    @JsonProperty
    @NotBlank(message = "Email is mandatory")
    @Email
    private String email;

    @JsonProperty
    private List<StateReqDto> stateReqDTOS ;

    @JsonProperty
    private String degree;

    @JsonProperty
    private String college;

    @JsonProperty
    private String yearOfPassing;

    @JsonProperty
    private List<LabDetailDto> associatedLab;

    @JsonProperty
    private List<DesignationReqDto> designationReqDTOS;

    @JsonProperty
    private String profileImg;

    @JsonProperty
    private boolean isPasswordChangeRequired;

    @JsonProperty
    private boolean isActive;

    @JsonProperty
    private List<RoleDto> roles;

}
