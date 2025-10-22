package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pathdx.dto.requestDto.CaseDetailsDto;
import com.pathdx.model.CaseDetails;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;

@Setter
@Getter
public class SlideDetailsDto {
    private Long id;
    private String barcodeId;
    private String blockId;
    private CaseDetailsDto caseDetail;
    private int rescanFlag;
    private String stain;
    private String specimenId;
    private String scannedDate;
    private Date createdDate;
    private Date lastModifiedDate;
}
