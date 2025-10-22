package com.pathdx.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenResponseDto {
    private String refreshToken;
    private String messge;
}
