package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ChangePasswordDto {
    @JsonProperty
    @NotBlank
    @Email
    String email;

    @JsonProperty
    String currentPassword;

    @JsonProperty
    String newPassword;

    @JsonProperty
    String confirmNewPassword;

}
