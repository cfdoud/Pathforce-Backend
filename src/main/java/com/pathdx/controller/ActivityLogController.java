package com.pathdx.controller;

import com.pathdx.dto.requestDto.ActivityLogDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.service.ActivityLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/activityLog")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ActivityLogController {

    @Autowired
    public ActivityLogService activityLogService;

    @RequestMapping(value = "/fetch", method = RequestMethod.POST)
    public ResponseEntity getActivityLog(@RequestBody ActivityLogDto activityLogDTO) {

        ResponseEntity<ResponseDto> responseEntity = null;
        ResponseDto responseDTO = activityLogService.getActivityLog(activityLogDTO);
        if (responseDTO.getStatusCode()==200) {
            responseEntity = new ResponseEntity<ResponseDto>(responseDTO, HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<ResponseDto>(responseDTO, HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
}
