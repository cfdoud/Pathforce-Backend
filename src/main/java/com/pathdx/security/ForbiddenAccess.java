package com.pathdx.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathdx.constant.Constants;
import com.pathdx.dto.responseDto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
@Slf4j
public class ForbiddenAccess implements AccessDeniedHandler, Serializable {
    private static final long serialVersionUID = -7858895553553243875L;

    private static final String forbiddenError = "Forbidden";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.debug("---ForbiddenAccess::handle---");
        ResponseDto responseDTO = new ResponseDto(false, forbiddenError, Constants.FORBIDDEN);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        byte[] body = new ObjectMapper()
                .writeValueAsBytes(responseDTO);
        response.getOutputStream().write(body);
    }
}
