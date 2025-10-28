package com.pathdx.service.impl;

import com.pathdx.dto.requestDto.ThumbnailDto;
import com.pathdx.dto.responseDto.*;
import com.pathdx.exception.LabNotFoundException;
import com.pathdx.service.CaseService;
import com.pathdx.utils.CaseListingStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Profile("local")
@Service
public class LocalCaseServiceImpl implements CaseService {
  @Override public ResponseDto<CaseImageResponseDto> getImagesForCase(String labId, String accessionId, String caseId, String email) { return null; }
  @Override public ResponseDto<CaseListingDto> getCaseListing(String userMail, String labId, CaseListingStatus status, Optional<String> accessionId, int pageNo, int pageSize) throws LabNotFoundException, Exception { return null; }
  @Override public Map<String, Long> getCaseCount(String userMail, String labId) { return java.util.Collections.emptyMap(); }
  @Override public String saveUserSlide(ThumbnailDto thumbnailDto, String email) throws Exception { return ""; }
  @Override public ResponseDto<CaseResponseDto> getCaseInfo(Optional<Long> caseId, Long orderMessageId, String labId) throws Exception { return null; }
}
