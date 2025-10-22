package com.pathdx.service;

import com.pathdx.dto.requestDto.AnnotatedImageReqDto;
import com.pathdx.dto.requestDto.ReportsReqDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.model.OrderMessages;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public interface ReportPdfService {
    ResponseDto<ReportResponseDto> createReport(ReportsReqDto reportDto) throws Exception;
    ResponseDto<byte[]> viewRequisitionPdf(ReportsReqDto reportModelDto);

    ResponseDto<ReportResponseDto> viewReportPdf(ReportsReqDto reportModelDto);

    ResponseDto<ReportResponseDto> saveAnnImage(AnnotatedImageReqDto annotatedImageReqDto) throws Exception;

}
