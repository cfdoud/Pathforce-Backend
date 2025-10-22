package com.pathdx.controller;

import com.pathdx.dto.requestDto.UserConfigReqDto;
import com.pathdx.dto.responseDto.CaseAllocationResponseDto;
import com.pathdx.dto.responseDto.CountAssignedAndUnAssigned;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.model.CaseAllocationConfigModel;
import com.pathdx.model.LabDetail;
import com.pathdx.service.CaseAllocationService;
import com.pathdx.service.LabDetailService;
import com.pathdx.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.pathdx.constant.Constants.SUCCESS_MESSAGE;
import static com.pathdx.constant.Constants.UNABLE_TO_PROCESS;

@RestController
@Validated
@RequestMapping("/allocation")
@Slf4j
public class CaseAllocationController {

    @Autowired
    CaseAllocationService caseAllocationService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @GetMapping("/users/{labId}")
    public ResponseEntity<ResponseDto> getAllLabUsers(@PathVariable("labId") String labId) throws Exception{
        LabDetail labDetail = new LabDetail();
        labDetail.setLabid(labId);
        ResponseDto<List<CaseAllocationResponseDto>> responseDto= caseAllocationService.getAllLabUsers(labDetail);

        if(responseDto != null)
            return ResponseEntity.ok(responseDto);
        else
            return ResponseEntity.badRequest().body(new ResponseDto(null, UNABLE_TO_PROCESS, SUCCESS_MESSAGE, HttpStatus.UNAUTHORIZED.value()));
    }

    @PostMapping("/updateUserConfig")
    public ResponseEntity<ResponseDto> updateUserConfig(@RequestBody UserConfigReqDto userConfigReqDto,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headers) throws Exception{
        try {
            String email = jwtTokenUtil.retrieveUserNameFromToken(headers);
            ResponseDto responseDto = caseAllocationService.updateUserConfig(userConfigReqDto, email);
            if (responseDto != null)
                return ResponseEntity.ok(responseDto);
            else
                return ResponseEntity.badRequest().body(new ResponseDto(null, UNABLE_TO_PROCESS, SUCCESS_MESSAGE, HttpStatus.UNAUTHORIZED.value()));
        }catch (Exception e){
            throw e;
        }
    }

    @PostMapping("/updateAllUserConfig")
    public ResponseEntity<ResponseDto> updateBulkUserConfig(@RequestBody List<UserConfigReqDto> userConfigReqDtoList) throws Exception{
        ResponseDto responseDto= caseAllocationService.updateBulkUserConfig(userConfigReqDtoList);
        if(responseDto!=null)
            return  ResponseEntity.ok(responseDto);
        else
            return ResponseEntity.badRequest().body(new ResponseDto(null, UNABLE_TO_PROCESS, SUCCESS_MESSAGE, HttpStatus.UNAUTHORIZED.value()));
    }
    @GetMapping("/casesCount/{labId}")
    public ResponseEntity<ResponseDto> getAssingedCasesCount(@PathVariable String labId){
        ResponseDto<CountAssignedAndUnAssigned> countList= caseAllocationService.getAssignedCasesCount(labId);
        return  ResponseEntity.ok(countList);
    }
    /*@GetMapping("test")
    public void test(){

        //caseAllocationService.casesAllocation();

        caseAllocationService.sendEmialForPendingCases();
    }*/

}
