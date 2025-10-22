package com.pathdx.dto.requestDto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestDto {

    @Email
    @NotBlank(message = "Name is mandatory")
    String email;

    @NotBlank(message = "password is mandatory")
    String password;
    Boolean ssoLogin;
}
