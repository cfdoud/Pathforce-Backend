package com.pathdx.service.impl;

import com.pathdx.dto.requestDto.CaseDetailsDto;
import com.pathdx.dto.requestDto.ThumbnailDto;
import com.pathdx.dto.responseDto.CaseCommentsDto;
import com.pathdx.dto.responseDto.CaseImageResponseDto;
import com.pathdx.dto.responseDto.CaseListingDto;
import com.pathdx.dto.responseDto.CaseResponseDto;
import com.pathdx.dto.responseDto.OrderMessageDto;
import com.pathdx.dto.responseDto.PatientsDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.service.CaseService;
import com.pathdx.utils.CaseListingStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@Profile("demo")
public class DemoCaseService implements CaseService {

    @Override
    public ResponseDto<CaseImageResponseDto> getImagesForCase(
            String labId,
            String accessionId,
            String caseId,
            String email
    ) {
        ResponseDto<CaseImageResponseDto> response = new ResponseDto<>();
        response.setResponse(new CaseImageResponseDto());
        response.setSuccessMsg("Demo mode: image content is not available");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    @Override
    public ResponseDto<CaseListingDto> getCaseListing(
            String userMail,
            String labId,
            CaseListingStatus status,
            Optional<String> accessionId,
            int pageNo,
            int pageSize
    ) {
        CaseDetailsDto demoCase = new CaseDetailsDto();
        demoCase.setId(1L);
        demoCase.setOrderMessageId(1000L);
        demoCase.setCaseId("DEMO-CASE-001");
        demoCase.setReportGenerated(false);

        Map<String, Set<CaseDetailsDto>> accessionMap = new HashMap<>();
        accessionMap.put("DEMO-ACCESSION", Collections.singleton(demoCase));

        CaseListingDto listingDto = new CaseListingDto(1L, accessionMap);

        ResponseDto<CaseListingDto> response = new ResponseDto<>();
        response.setResponse(listingDto);
        response.setSuccessMsg("Demo mode: returning sample case listing");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    @Override
    public Map<String, Long> getCaseCount(String userMail, String labId) {
        Map<String, Long> counts = new HashMap<>();
        counts.put(CaseListingStatus.NEWCASES.name(), 1L);
        counts.put(CaseListingStatus.CLOSEDCASES.name(), 0L);
        counts.put(CaseListingStatus.ALLCASES.name(), 1L);
        return counts;
    }

    @Override
    public String saveUserSlide(ThumbnailDto thumbnailDto, String email) {
        return "Demo mode: slide saved for " + email;
    }

    @Override
    public ResponseDto<CaseResponseDto> getCaseInfo(Optional<Long> caseId, Long orderMessageId, String labId) {
        CaseResponseDto caseResponseDto = new CaseResponseDto();
        caseResponseDto.setAssignedUser("demo.user@pathforce.com");
        caseResponseDto.setPatient(new PatientsDto(
                "Demo Patient",
                "1990-01-01",
                "Other",
                "Demo Ethnicity",
                "ACC-001",
                "MRN-001"
        ));

        caseResponseDto.setCaseComments(new CaseCommentsDto(
                List.of("Initial demo diagnosis"),
                List.of("Demo case summary"),
                List.of("Demo clinical history"),
                List.of("Demo final diagnosis")
        ));

        caseResponseDto.setOrderMessage(new OrderMessageDto(
                "Demo Lab",
                orderMessageId,
                "Demo Client",
                "INPROCESS",
                "ACC-001",
                "2023-01-01",
                "ACC-001"
        ));

        caseResponseDto.setObservations(Collections.singletonMap("observation", "Demo observation value"));
        caseResponseDto.setPhysicianName("Demo Physician");
        caseResponseDto.setPhysicianPhone("555-0100");
        caseResponseDto.setReportGeneratedFlag(false);

        try {
            caseResponseDto.setReqPdfSingedUrl(new URL("https://example.com/demo.pdf"));
        } catch (MalformedURLException ignored) {
            // The fallback URL is well-formed, so this block will not be hit in practice.
        }

        ResponseDto<CaseResponseDto> response = new ResponseDto<>();
        response.setResponse(caseResponseDto);
        response.setSuccessMsg("Demo mode: returning sample case details");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }
}

