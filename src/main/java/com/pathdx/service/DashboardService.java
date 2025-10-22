package com.pathdx.service;

import com.pathdx.dto.requestDto.CaseToUserDto;
import com.pathdx.dto.responseDto.DashboardResponseDto;
import com.pathdx.dto.responseDto.YearWiseCaseCount;
import com.pathdx.utils.CaseStatus;
import com.pathdx.utils.DashboardSort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface DashboardService {
    String assignCaseToUser(CaseToUserDto caseToUserDto, String email) throws Exception;

    Map<String, Long> getCaseList(String labId, String email) throws ParseException;

    DashboardResponseDto getCaseByStatus(String labId, CaseStatus status, int pageNo, int pageSize, String s, DashboardSort sort, String order, Map<String, String> filter, Optional<String> date, Optional<Integer> age) throws ParseException;

    Map<String, List<YearWiseCaseCount>> getMyActivity(String labId, String s);

   // void getCaseInfo(String labId, Map<String, String> filter, Optional<Date> date, Optional<Integer> aging, int pageNo, int pageSize, DashboardSort sort, String order);
}
