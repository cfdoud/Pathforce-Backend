package com.pathdx.controller;

import com.pathdx.constant.UtilConstants;
import com.pathdx.dto.requestDto.CaseDetailsDto;
import com.pathdx.dto.requestDto.ChangePasswordDto;
import com.pathdx.dto.requestDto.ThumbnailDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.exception.LabNotFoundException;
import com.pathdx.exception.OrderMessageNotFoundException;
import com.pathdx.service.CaseService;
import com.pathdx.utils.CaseListingStatus;
import com.pathdx.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.pathdx.constant.UtilConstants.*;

@RestController
@Validated
@RequestMapping("/case")
@Slf4j
public class CaseController {

    @Autowired
    CaseService caseService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;



    @GetMapping("/details/{labId}/{orderMessageId}")
    public ResponseEntity<ResponseDto> getCaseInfo(@PathVariable String labId,@PathVariable Long orderMessageId,
                                                   @RequestParam(value="requestId", required = false) Optional<Long> caseId)
            throws OrderMessageNotFoundException,Exception{
        ResponseDto<CaseResponseDto> responseDto = new ResponseDto<>();
        try {
            log.info("provided inputs to get list of accessionids: labId is {}, ordermessageid is {} ", labId, orderMessageId);
            responseDto = caseService.getCaseInfo(caseId, orderMessageId,labId);
            log.info("response is {}", responseDto.toString());
        }
        catch (OrderMessageNotFoundException oe){
            throw oe;
        }
        catch (Exception e){
            throw e;
        }

        return ResponseEntity.ok(new ResponseDto(responseDto.getResponse(), "", UtilConstants.SUCCESS_MESSAGE, HttpStatus.OK.value()));
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/images")
    public ResponseEntity<ResponseDto> getFileListing(@RequestParam("labId") String labId,@RequestParam("accessionId")
    String accessionId , @RequestParam("caseId") String caseId, @RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headers) {
        log.info("Enter in getfilterListing");
        String email = jwtTokenUtil.retrieveUserNameFromToken(headers);
        ResponseDto<CaseImageResponseDto> responseDto = caseService.getImagesForCase(labId,accessionId,caseId, email);

        return ResponseEntity.ok(responseDto);
    }
    @GetMapping("/listing/{userMail}/{labId}")
    public ResponseEntity<ResponseDto> getAccessionIdsforUser( @PathVariable String userMail,
                                               @RequestParam("status") CaseListingStatus status,
                                               @RequestParam(defaultValue = "0") int pageNo,
                                               @RequestParam(defaultValue = "10") int pageSize,
                                               @PathVariable String labId,
                                               @RequestParam(value = "accessionId", required = false) Optional<String>
                                                                           accessionId) throws LabNotFoundException,Exception {
        ResponseDto<CaseListingDto> responseDto = null;
        try{
            log.info("provided inputs to get list of accessionids: userEmail is {}, status is {} ", userMail, status);
           responseDto= caseService.getCaseListing(userMail, labId, status, accessionId, pageNo, pageSize);
           log.info("response is {}", responseDto.toString());
            }
        catch (LabNotFoundException le){
            throw le;
        }
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/count/{labId}")
    public ResponseEntity<ResponseDto> getCaseCount(  @PathVariable String labId,
                                                      @RequestParam(value = "usermail", required = false) Optional<String> usermail,
                                                      @RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String> headers){
        String loggedInmail = jwtTokenUtil.retrieveUserNameFromToken(headers);
        if(usermail.isPresent()){
            log.info("In case count api: given usermail is {}", usermail.get());
      return ResponseEntity.ok(new ResponseDto<>().withResponse(caseService.getCaseCount(usermail.get(), labId)));}
        else{
            log.info("In case count api: logged in usermail is {}", loggedInmail);
            return ResponseEntity.ok(new ResponseDto<>().withResponse(caseService.getCaseCount(loggedInmail, labId)));
        }

    }
    @PostMapping("/thumbnailviewer")
    public ResponseEntity<ResponseDto> saveUserSlide(@RequestHeader(HttpHeaders.AUTHORIZATION) Map<String, String>
                                                                  headers,@RequestBody ThumbnailDto thumbnailDto) throws Exception {
        String email = jwtTokenUtil.retrieveUserNameFromToken(headers);
        return ResponseEntity.ok(new ResponseDto(caseService.saveUserSlide(thumbnailDto, email), "", SUCCESS_MESSAGE, HttpStatus.OK.value()));

    }

}
