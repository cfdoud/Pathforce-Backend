package com.pathdx.service.impl;

import com.pathdx.constant.AuditLogSort;
import com.pathdx.dto.requestDto.LabDetailDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.service.AuditLogsService;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Profile("demo")
@Primary
@Service
public class DemoAuditLogsService implements AuditLogsService {

    @Override
    public ResponseDto<List<LabDetailDto>> getAllLabs() {
        ResponseDto<List<LabDetailDto>> resp = new ResponseDto<>(true, "OK", null);
        List<LabDetailDto> labs = new ArrayList<>();
        // minimal fake data
        LabDetailDto d = new LabDetailDto();
        d.setLabId("DEMO-LAB");
        d.setLabName("Demo Laboratory");
        labs.add(d);
        resp.setResponse(labs);
        return resp;
    }

    @Override
    public AuditLogResponseDto getAllAuditLogs(String labId,
                                               Optional<Long> userId,
                                               Date fromDate,
                                               Date toDate,
                                               int firstRow,
                                               int maxRow) throws ParseException {
        // Return an empty-but-valid page
        AuditLogResponseDto out = new AuditLogResponseDto();
        out.setTotalCount(0L);
        out.setTotalNoOfPages(0);
        out.setAuditLogDtos(Collections.emptyList());
        return out;
    }

    @Override
    public AuditLogResponseDto getAuditLogByFilter(String labId,
                                                   Optional<Long> userId,
                                                   Date fromDate,
                                                   Date toDate,
                                                   int pageNo,
                                                   int pageSize,
                                                   AuditLogSort sort,
                                                   String order,
                                                   Map<String, String> parameters,
                                                   Optional<String> date) throws ParseException {
        AuditLogResponseDto out = new AuditLogResponseDto();
        out.setTotalCount(0L);
        out.setTotalNoOfPages(0);
        out.setAuditLogDtos(Collections.emptyList());
        return out;
    }

    @Override
    public List<AuditLogRespDto> getAllAuditLogs(String labId,
                                                 Optional<Long> userId,
                                                 Date fromDate,
                                                 Date toDate) throws ParseException {
        // Non-paged variant: return empty list
        return Collections.emptyList();
    }
}
