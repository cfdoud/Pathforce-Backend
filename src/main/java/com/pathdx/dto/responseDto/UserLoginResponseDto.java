package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pathdx.dto.requestDto.RoleDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginResponseDto {

    private String message;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profileImg;
    private String jwtToken;
    private Boolean isSso;
    private Boolean changePassword;
    private boolean isActive;
    private List<RoleDto> roles;

}
