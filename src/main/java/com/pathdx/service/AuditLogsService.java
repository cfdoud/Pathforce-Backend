package com.pathdx.service;

import com.pathdx.constant.AuditLogSort;
import com.pathdx.dto.requestDto.LabDetailDto;
import com.pathdx.dto.responseDto.AuditLogRespDto;
import com.pathdx.dto.responseDto.AuditLogResponseDto;
import com.pathdx.dto.responseDto.ResponseDto;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface AuditLogsService {

    public ResponseDto<List<LabDetailDto>> getAllLabs();

    public AuditLogResponseDto getAllAuditLogs(String labId, Optional<Long> userId, Date fromDate, Date toDate, int firstRow, int maxRow) throws ParseException;

    public AuditLogResponseDto getAuditLogByFilter(String labId, Optional<Long> userId, Date fromDate, Date toDate, int pageNo, int pageSize, AuditLogSort sort, String order, Map<String, String> parameters, Optional<String> date) throws ParseException;

    public List<AuditLogRespDto> getAllAuditLogs(String labId,Optional<Long> userId, Date fromDate, Date toDate) throws ParseException;
}
