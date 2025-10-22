package com.pathdx.controller;

import com.pathdx.dto.requestDto.EmailModelDto;
import com.pathdx.dto.requestDto.SlideDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.service.SlideDetailService;
import com.pathdx.service.StainsService;
import com.pathdx.service.UserService;
import com.pathdx.utils.CommonUtil;
import com.pathdx.utils.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pathdx.constant.Constants.ADDITION_REQUEST_BODY_Start;
import static com.pathdx.constant.UtilConstants.*;

@RestController
@RequestMapping("/slideDetail")
@Slf4j
public class SlideDetailsController {

    @Autowired
    private SlideDetailService slideDetailService;

    @Autowired
    private StainsService stainsService;

    @GetMapping("/{caseDetailId}")
    public ResponseEntity<ResponseDto> getSlideDetails(@PathVariable("caseDetailId") Long caseDetailId) {
        ResponseDto<List<SlideDetailsDto>> lstSlideDetails = slideDetailService.getSlideDetails(caseDetailId);
        return ResponseEntity.ok(lstSlideDetails);
    }

    @GetMapping("/stains")
    public ResponseEntity<ResponseDto> getAllStains(){
        ResponseDto<StainsResponseDto> responseDto = stainsService.getAllStains();

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/getdata")
    public ResponseEntity<ResponseDto> listImageDetails(@RequestParam ("barcode") String barCode,@RequestParam (required = false) String labid ,@RequestParam(required = false) String accessionId, @RequestParam(required = false) String caseId){
        ResponseDto data= slideDetailService.getData(barCode,labid ,accessionId ,caseId);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/save/{accessionId}/{emailId}")
    public ResponseEntity<ResponseDto> saveSlideDetails(@RequestBody SlideDto slideDto,
                                                        @PathVariable("accessionId") String accessionId,
                                                        @PathVariable("emailId") String emailId) {
        ResponseDto<SlideResDto> resSlideDto = slideDetailService.saveSlideDetails(slideDto);
        resSlideDto = stainsService.saveStainsAndStainPanel(slideDto, resSlideDto, emailId, accessionId);

        return ResponseEntity.ok(resSlideDto);
    }
}
