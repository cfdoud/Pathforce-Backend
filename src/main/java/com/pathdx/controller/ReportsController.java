package com.pathdx.controller;

import com.pathdx.dto.requestDto.AnnotatedImageReqDto;
import com.pathdx.dto.requestDto.ReportsReqDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.service.*;
import com.pathdx.utils.GoogleCloudStorageUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import static com.pathdx.constant.Constants.SUCCESS_MESSAGE;
import static com.pathdx.constant.Constants.UNABLE_TO_PROCESS;

@RestController
@RequestMapping("/reports")
@Slf4j
public class ReportsController {

    @Autowired
    ReportPdfService reportPdfService;

    @PostMapping("/pdfGenerate")
    public ResponseEntity<ResponseDto> generatePdf(@RequestBody ReportsReqDto reportModelDto) throws Exception{
        log.info("Started PDF generation");

        ResponseDto<ReportResponseDto> responseDto = reportPdfService.createReport(reportModelDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=pathforcereport.pdf");
        return ResponseEntity.ok(responseDto);

    }

    @PostMapping("/viewPdfReport")
    public ResponseEntity<ResponseDto> viewGeneratedPdf(@RequestBody ReportsReqDto reportModelDto){
        log.info("Started view pdf report");
        ResponseDto<ReportResponseDto> responseDto = reportPdfService.viewReportPdf(reportModelDto);

        log.info("Ending view pdf report");
        if(responseDto!=null){
            return ResponseEntity.ok(responseDto);
        }else{
            return ResponseEntity.badRequest().body(new ResponseDto(null, UNABLE_TO_PROCESS, SUCCESS_MESSAGE, HttpStatus.UNAUTHORIZED.value()));
        }
    }

    @PostMapping("/viewReqPdf")
    public ResponseEntity<InputStreamResource> viewRequisitionPdf(@RequestBody ReportsReqDto reportModelDto){
        log.info("Started view Requisition Pdf");

        ResponseDto<byte[]> responseDto = reportPdfService.viewRequisitionPdf(reportModelDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=pathforcereport.pdf");
        log.info("Ending view Requisition Pdf");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(new ByteArrayInputStream(responseDto.getResponse())));
    }

    @PostMapping("/saveAnnotatedImage")
    public ResponseEntity<ResponseDto> saveAnnotatedImage(@RequestBody AnnotatedImageReqDto annotatedImageReqDto) throws Exception{
        log.info("Started save Annotate dImage");
        ResponseDto<ReportResponseDto> responseDto = reportPdfService.saveAnnImage(annotatedImageReqDto);
        log.info("Ending  save Annotate dImage");
        if(responseDto!=null){
            return ResponseEntity.ok(responseDto);
        }else{
            return ResponseEntity.badRequest().body(new ResponseDto(null, UNABLE_TO_PROCESS, SUCCESS_MESSAGE, HttpStatus.UNAUTHORIZED.value()));
        }
    }


    //Rest Service to download excel apache poi
}
