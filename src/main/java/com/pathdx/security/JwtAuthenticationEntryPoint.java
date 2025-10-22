package com.pathdx.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathdx.constant.Constants;
import com.pathdx.dto.responseDto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String errorMessage = response.getHeader("error");
        ResponseDto responseDTO = new ResponseDto(false, errorMessage, Constants.UNAUTHORIZED);
     //   log.error("Invalid Token" + responseDTO.toString());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        byte[] body = new ObjectMapper()
                .writeValueAsBytes(responseDTO);
        response.getOutputStream().write(body);
    }
}