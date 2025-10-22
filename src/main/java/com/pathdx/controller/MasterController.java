package com.pathdx.controller;

import com.pathdx.dto.requestDto.LabDetailsRequestDto;
import com.pathdx.dto.requestDto.LabHeadingsReqDto;
import com.pathdx.dto.responseDto.LabHeadingsResponseDto;
import com.pathdx.dto.responseDto.LabResponseDto;
import com.pathdx.dto.responseDto.MasterResponseDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.model.LabHeadings;
import com.pathdx.service.LabDetailService;
import com.pathdx.service.LabHeadingService;
import com.pathdx.service.MasterService;
import com.pathdx.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Entities;
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
@RequestMapping("/master")
@Slf4j
public class MasterController {

    @Autowired
    private MasterService masterService;
    @Autowired
    LabDetailService labDetailService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    LabHeadingService labHeadingService;

    @GetMapping("/designation")
    public ResponseEntity<ResponseDto> getAllDesignation() {
        ResponseDto<List<MasterResponseDto>> responseDto= masterService.getAllDesignation();
        return  ResponseEntity.ok(new ResponseDto(responseDto,"", SUCCESS_MESSAGE , HttpStatus.OK.value()));
    }

   /* @GetMapping("/associatedLab")
    public ResponseEntity<ResponseDto> getAllAssociatedLab() {
        ResponseDto<List<MasterResponseDto>> responseDto = masterService.getAllAssociatedLab();
        return  ResponseEntity.ok(new ResponseDto(responseDto,"", SUCCESS_MESSAGE , HttpStatus.OK.value()));
    }*/

    @GetMapping("/state")
    public ResponseEntity<ResponseDto> getAllState() {
        ResponseDto<List<MasterResponseDto>> responseDto = masterService.getAllState();
        return ResponseEntity.ok(new ResponseDto(responseDto, "", SUCCESS_MESSAGE, HttpStatus.OK.value()));
    }

    @GetMapping("/labs")
    public ResponseEntity<ResponseDto> getAllLabs() throws Exception {
        ResponseDto<List<MasterResponseDto>> responseDto = labDetailService.getAllLabs();
        if(responseDto != null)
            return ResponseEntity.ok(responseDto);
        else
            return ResponseEntity.badRequest().body(new ResponseDto(null, UNABLE_TO_PROCESS, SUCCESS_MESSAGE, HttpStatus.UNAUTHORIZED.value()));

    }

    @GetMapping("/allLabsDetails")
    public ResponseEntity<ResponseDto> getAllLabsDetails() throws Exception {
         ResponseDto<List<LabResponseDto>> responseDto = labDetailService.getAllLabsDetails();
        if(responseDto != null)
            return ResponseEntity.ok(responseDto);
        else
            return ResponseEntity.badRequest().body(new ResponseDto(null, UNABLE_TO_PROCESS, SUCCESS_MESSAGE, HttpStatus.UNAUTHORIZED.value()));
    }

    @GetMapping("/labs/{labId}")
    public ResponseEntity<ResponseDto> getLabDetails(@PathVariable("labId") String labId) throws Exception {
        ResponseDto<LabResponseDto> responseDto = labDetailService.getLabDetails(labId);
        if(responseDto != null)
            return ResponseEntity.ok(responseDto);
        else
            return ResponseEntity.badRequest().body(new ResponseDto(null, UNABLE_TO_PROCESS, SUCCESS_MESSAGE, HttpStatus.UNAUTHORIZED.value()));
    }

    @PostMapping("/labs/save")
    public ResponseEntity<ResponseDto> updateLabDetails(@RequestBody LabDetailsRequestDto labDetailsRequestDto,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headers) throws Exception {
        try {
            String email = jwtTokenUtil.retrieveUserNameFromToken(headers);
            ResponseDto<LabResponseDto> responseDto = labDetailService.saevLabDetails(labDetailsRequestDto, email);
            if (responseDto != null)
                return ResponseEntity.ok(responseDto);
            else
                return ResponseEntity.badRequest().body(new ResponseDto(null, UNABLE_TO_PROCESS, SUCCESS_MESSAGE, HttpStatus.UNAUTHORIZED.value()));
        }catch (Exception e){
            throw e;
        }
    }
    @GetMapping("/labs/associated")
    public ResponseEntity<ResponseDto> getAssociatedLabs( @RequestHeader(HttpHeaders.AUTHORIZATION) Map<String,String> headers) throws Exception {
        ResponseDto<List<MasterResponseDto>> responseDto = labDetailService.getAssociatedlabs(jwtTokenUtil.retrieveUserNameFromToken(headers));
        if(responseDto != null)
            return ResponseEntity.ok(responseDto);
        else
            return ResponseEntity.badRequest().body(new ResponseDto(null, UNABLE_TO_PROCESS, SUCCESS_MESSAGE, HttpStatus.UNAUTHORIZED.value()));

    }

    @GetMapping("/roles")
    public ResponseEntity<ResponseDto> getAllRoles() throws Exception {
        ResponseDto<List<MasterResponseDto>> responseDto = masterService.getAllRoles();
        if(responseDto != null)
            return ResponseEntity.ok(responseDto);
        else
            return ResponseEntity.badRequest().body(new ResponseDto(null, UNABLE_TO_PROCESS, SUCCESS_MESSAGE, HttpStatus.UNAUTHORIZED.value()));

    }

    @GetMapping("labHeadings/{labId}")
    public ResponseEntity<ResponseDto> getLabHeadings(@PathVariable("labId") String labId) throws Exception{
        ResponseDto<LabHeadingsResponseDto> responseDto = labHeadingService.getLabHeadingDetails(labId);
        if(responseDto != null)
            return ResponseEntity.ok(responseDto);
        else
            return ResponseEntity.badRequest().body(new ResponseDto(null, UNABLE_TO_PROCESS, SUCCESS_MESSAGE, HttpStatus.UNAUTHORIZED.value()));

    }

    @PostMapping("labHeadings/save")
    public ResponseEntity<ResponseDto> updateLabHeadings(@RequestBody LabHeadingsReqDto labHeadingsReqDto) throws Exception{
        ResponseDto<LabHeadingsResponseDto> responseDto = labHeadingService.updateLabHeadings(labHeadingsReqDto);
        if(responseDto != null)
            return ResponseEntity.ok(responseDto);
        else
            return ResponseEntity.badRequest().body(new ResponseDto(null, UNABLE_TO_PROCESS, SUCCESS_MESSAGE, HttpStatus.UNAUTHORIZED.value()));

    }

}
