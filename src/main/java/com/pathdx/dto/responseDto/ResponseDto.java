package com.pathdx.dto.responseDto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@NoArgsConstructor
public class ResponseDto<T> {
    private T response;
    private String error;
    private String successMsg;
    private int statusCode;

    public ResponseDto(T response, String error, String successMsg, int statusCode) {
        this.response = response;
        this.error = error;
        this.successMsg = successMsg;
        this.statusCode = statusCode;
    }

    public ResponseDto(boolean b, String errorMessage, String unauthorized) {
    }

}
