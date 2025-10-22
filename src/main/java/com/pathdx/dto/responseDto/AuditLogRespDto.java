package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLogRespDto {

    private Long auditLogId;
    private String accessionId;
    private String firstName;
    private String lastName;
    private String emailId;
    private String caseStatus;
    private String actionType;
    private String description;
    private Date dateAndTime;

    /*public AuditLogRespDto(Long auditLogId, String accessionId, String firstName, String description, String actionType) {
        this.auditLogId = auditLogId;
        this.accessionId = accessionId;
        this.firstName = firstName;
        this.description = description;
        this.actionType = actionType;
    }*/
}
