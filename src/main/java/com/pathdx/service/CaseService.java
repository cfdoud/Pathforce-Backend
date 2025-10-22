package com.pathdx.service;

import com.pathdx.dto.requestDto.ThumbnailDto;
import com.pathdx.dto.responseDto.CaseImageResponseDto;
import com.pathdx.dto.responseDto.CaseListingDto;
import com.pathdx.dto.responseDto.CaseResponseDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.exception.LabNotFoundException;
import com.pathdx.utils.CaseListingStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public interface CaseService {

    ResponseDto<CaseImageResponseDto> getImagesForCase(String labId, String accessionId, String caseId, String email);

    ResponseDto<CaseListingDto> getCaseListing(String userMail, String labId, CaseListingStatus status, Optional<String> accessionId, int pageNo, int pageSize) throws LabNotFoundException,Exception;

    Map<String, Long> getCaseCount(String userMail, String labId);

   // ResponseDto<CaseResponseDto> getCaseInfo(Optional<Long> caseId, Long orderMessageId) throws Exception;

    String saveUserSlide(ThumbnailDto thumbnailDto, String email) throws Exception;
    ResponseDto<CaseResponseDto> getCaseInfo(Optional<Long> caseId, Long orderMessageId,String labId) throws Exception;
}
