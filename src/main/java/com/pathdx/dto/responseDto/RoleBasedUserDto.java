package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleBasedUserDto {
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String email;
    private List<DesignationResponseDto> designation;
    private boolean isActive;
    private Long role;
    private Date lastLogin;

}
