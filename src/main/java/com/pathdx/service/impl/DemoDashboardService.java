package com.pathdx.service.impl;

import com.pathdx.dto.requestDto.CaseToUserDto;
import com.pathdx.dto.responseDto.DashboardDto;
import com.pathdx.dto.responseDto.DashboardResponseDto;
import com.pathdx.dto.responseDto.YearWiseCaseCount;
import com.pathdx.service.DashboardService;
import com.pathdx.utils.CaseStatus;
import com.pathdx.utils.DashboardSort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Profile("demo")
public class DemoDashboardService implements DashboardService {

    @Override
    public String assignCaseToUser(CaseToUserDto caseToUserDto, String email) {
        return "Demo mode: " + caseToUserDto.getLabId() + " case assigned to " + caseToUserDto.getUserMail();
    }

    @Override
    public Map<String, Long> getCaseList(String labId, String email) {
        Map<String, Long> caseCounts = new HashMap<>();
        caseCounts.put(CaseStatus.NEWCASES.name(), 1L);
        caseCounts.put(CaseStatus.INPROCESSCASES.name(), 2L);
        caseCounts.put(CaseStatus.PENDINGCASES.name(), 0L);
        caseCounts.put(CaseStatus.CLOSEDCASES.name(), 5L);
        return caseCounts;
    }

    @Override
    public DashboardResponseDto getCaseByStatus(
            String labId,
            CaseStatus status,
            int pageNo,
            int pageSize,
            String search,
            DashboardSort sort,
            String order,
            Map<String, String> filter,
            Optional<String> date,
            Optional<Integer> age
    ) {
        DashboardDto dashboardDto = new DashboardDto(
                1000L,
                "ACC-001",
                "Demo Hospital",
                "Demo Patient",
                "Demo Physician",
                3L,
                null,
                true,
                1L
        );

        DashboardResponseDto responseDto = new DashboardResponseDto(
                List.of(dashboardDto),
                1,
                1L
        );
        return responseDto;
    }

    @Override
    public Map<String, List<YearWiseCaseCount>> getMyActivity(String labId, String email) {
        YearWiseCaseCount count = new YearWiseCaseCount(2023, 1, 12);
        return Collections.singletonMap("DEMO", List.of(count));
    }
}

