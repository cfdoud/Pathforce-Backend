package com.pathdx.controller;

import com.pathdx.constant.UtilConstants;
import com.pathdx.dto.requestDto.CaseToUserDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.model.OrderMessages;
import com.pathdx.service.DashboardService;
import com.pathdx.service.UserService;
import com.pathdx.utils.CaseStatus;
import com.pathdx.utils.DashboardSort;
import com.pathdx.utils.JwtTokenUtil;
import io.micrometer.core.ipc.http.HttpSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;

@RestController
@Validated
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @Autowired
    UserService userService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @GetMapping("/usersList/{labId}")
    public ResponseEntity<ResponseDto> assignUsersList(@PathVariable String labId) throws Exception{
        ResponseDto<Set<String>> responseDto = new ResponseDto<>();
        try {
            Set<String> users = userService.getassignUsers(labId);
            responseDto.setResponse(users);
        }
        catch (Exception e){
            throw e;
        }

        return ResponseEntity.ok(new ResponseDto(responseDto.getResponse(), "", UtilConstants.SUCCESS_MESSAGE,
                HttpStatus.OK.value()));
    }

    @PostMapping("/assigncasetouser")
    public ResponseEntity<ResponseDto> assignCaseToUser(@RequestBody CaseToUserDto caseToUserDto,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headers)
            throws Exception{

        try {
            String email = jwtTokenUtil.retrieveUserNameFromToken(headers);
            return ResponseEntity.ok(new ResponseDto(dashboardService.assignCaseToUser(caseToUserDto, email),"",
                    UtilConstants.SUCCESS_MESSAGE, HttpStatus.OK.value()));
        }
        catch (Exception e){
            throw e;
        }

    }

    @GetMapping("/caselistingbystatus/{labId}")
    public ResponseEntity<ResponseDto> getCasesByStatus(@PathVariable String labId,
                                                        @RequestParam(value = "status") CaseStatus status,
                                                        @RequestParam(defaultValue = "1") int pageNo,
                                                        @RequestParam(defaultValue = "20") int pageSize,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) Map<String,String> headers,
                                                        @RequestParam(defaultValue = "createddate") DashboardSort sort,
                                                        @RequestParam Map<String, String> parameters,
                                                        @RequestParam(value = "date", required = false)  Optional<String> date,
                                                        @RequestParam(value = "age", required = false) Optional<Integer> age) throws ParseException {
        ResponseDto<DashboardResponseDto> responseDto = new ResponseDto<>();
        pageNo--;
        DashboardResponseDto dashboardResponseDto = new DashboardResponseDto();
        if(parameters.containsKey("userMail")){
            try {
                dashboardResponseDto   = dashboardService.getCaseByStatus(labId,status, pageNo, pageSize, parameters.get("userMail"), sort, parameters.get("order"), parameters, date, age);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            dashboardResponseDto = dashboardService.getCaseByStatus(labId, status, pageNo, pageSize, jwtTokenUtil.retrieveUserNameFromToken(headers), sort, parameters.get("order"), parameters
                    , date, age);
        }
    responseDto.setResponse(dashboardResponseDto);
        return ResponseEntity.ok(responseDto);
    }
    @GetMapping("/caselisting/{labId}")
    public ResponseEntity<ResponseDto> getCaseList(@PathVariable String labId,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headers,
                                                   @RequestParam(required = false) Optional<String> userMail) throws ParseException {
        String email= jwtTokenUtil.retrieveUserNameFromToken(headers);
        ResponseDto<Map<String, Long>> responseDto = new ResponseDto<>();
        Map<String, Long> casesOnStatus = new HashMap<>();
        if(userMail.isPresent()){
            casesOnStatus = dashboardService.getCaseList(labId, userMail.get());
        }else{
       casesOnStatus = dashboardService.getCaseList(labId, email);}
        responseDto.setResponse(casesOnStatus);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/myactivity/{labId}")
    public ResponseEntity<ResponseDto> getMyActivity(@PathVariable String labId,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) Map<String,String> headers,
                                                     @RequestParam(value = "userMail", required = false) Optional<String> email){
        ResponseDto<Map<String, List<YearWiseCaseCount>>> responseDto = new ResponseDto<>();
        Map<String, List<YearWiseCaseCount>> yearWiseCaseCounts = new HashMap<>();
        if(email.isPresent()){
          yearWiseCaseCounts =   dashboardService.getMyActivity(labId, email.get());
        }else{
          yearWiseCaseCounts =  dashboardService.getMyActivity(labId, jwtTokenUtil.retrieveUserNameFromToken(headers));
        }
        responseDto.setResponse(yearWiseCaseCounts);
        return ResponseEntity.ok(responseDto);
    }


}
