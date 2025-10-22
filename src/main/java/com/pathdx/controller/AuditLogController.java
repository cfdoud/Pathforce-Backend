package com.pathdx.controller;

import com.pathdx.ExcelGeneration.ExcelGenerator;
import com.pathdx.constant.AuditLogSort;
import com.pathdx.dto.requestDto.LabDetailDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.model.AuditLogModel;
import com.pathdx.model.LabDetail;
import com.pathdx.service.AuditLogsService;
import com.pathdx.service.UserService;
import com.pathdx.utils.DashboardSort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/audit")
@Slf4j
public class AuditLogController {

    @Autowired
    AuditLogsService auditLongsService;

    @Autowired
    UserService userService;


    @GetMapping("/labDetails")
    public ResponseEntity<ResponseDto> getAllLabs() {
        ResponseDto<List<LabDetailDto>> responseDto = auditLongsService.getAllLabs();
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/users/{labId}")
    public ResponseEntity<ResponseDto> getAllUsersByLabId(@PathVariable("labId") String labId) {
        ResponseDto<List<UserModelResponseDto>> responseDto = new ResponseDto<>();
        LabDetail labDetail = new LabDetail();
        labDetail.setLabid(labId);
        List<UserModelResponseDto> lstUserModelResDto = userService.getUsersByLabId(labDetail);
        responseDto.setResponse(lstUserModelResDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/view/{fromDate}/{toDate}")
    public ResponseEntity<ResponseDto> getAuditLogs(@RequestParam("labId") String labId,
                                                    @RequestParam(value = "userId", required = false) Optional<Long> userId,
                                                    @PathVariable("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                                                    @PathVariable("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
                                                    @RequestParam(value = "firstRow", required = false) int firstRow,
                                                    @RequestParam(value = "maxRow", required = false) int maxRow) throws ParseException {
        ResponseDto<AuditLogResponseDto> responseDto = new ResponseDto<>();
        AuditLogResponseDto auditLogResponseDto = new AuditLogResponseDto();
        auditLogResponseDto = auditLongsService.getAllAuditLogs(labId, userId, fromDate, toDate, firstRow, maxRow);

        responseDto.setResponse(auditLogResponseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/view/filter")
    public ResponseEntity<ResponseDto> getAuditLogsByFilter(@RequestParam("labId") String labId,
                                                            @RequestParam(value = "userId", required = false) Optional<Long> userId,
                                                            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                                                            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
                                                            @RequestParam(defaultValue = "0") int pageNo,
                                                            @RequestParam(defaultValue = "20") int pageSize,
                                                            @RequestParam(defaultValue = "createddate") AuditLogSort sort,
                                                            @RequestParam Map<String, String> parameters,
                                                            @RequestParam(value = "date", required = false) Optional<String> date
    ) throws ParseException {

        ResponseDto<AuditLogResponseDto> responseDto = new ResponseDto<>();

        AuditLogResponseDto auditLogResponseDto = new AuditLogResponseDto();
        auditLogResponseDto = auditLongsService.getAuditLogByFilter(labId, userId, fromDate, toDate, pageNo, pageSize, sort, parameters.get("order"), parameters, date);

        responseDto.setResponse(auditLogResponseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/export/excel/{fromDate}/{toDate}")
    public void exportToExcel(HttpServletResponse response,
                              @RequestParam("labId") String labId,
                              @RequestParam(value = "userId", required = false) Optional<Long> userId,
                              @PathVariable("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                              @PathVariable("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) throws IOException, ParseException {
        try {
            log.info("Started exportToExcel");
            response.setContentType("application/octet-stream");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date dateObj = null;
            try {
                dateObj = sdf.parse(sdf.format(new Date()));
            } catch (java.text.ParseException e) {
//               log.("Exception occured" + e.printStackTrace());
                log.error(e.getMessage());
            }
//         dateObj = sdf.format(new Date());

            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=AuditLogs_" + dateObj.toString() + ".xlsx";
            response.setHeader(headerKey, headerValue);

            List<AuditLogRespDto> auditLogs = auditLongsService.getAllAuditLogs(labId,userId, fromDate, toDate);

            ExcelGenerator excelExporter = new ExcelGenerator(auditLogs);

            excelExporter.export(response);
            log.info("Ended exportToExcel");
        } catch (Exception e) {
            log.info(e + " ");
            log.error(e.getMessage());
        }
    }

}
