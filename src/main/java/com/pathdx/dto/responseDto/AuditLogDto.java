package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogDto {
    private Long auditLogId;
    private String accessionId;
    private String name;
    private String emailId;
    private String caseStatus;
    private String actionType;
    private String description;
    private Date createdDate;
}
