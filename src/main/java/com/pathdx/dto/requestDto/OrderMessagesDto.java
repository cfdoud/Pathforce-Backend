package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link com.pathdx.model.OrderMessages} entity
 */
@Data
public class OrderMessagesDto implements Serializable {
    @JsonProperty
    private  Long id;

    @JsonProperty
    private  String npi;

    @JsonProperty
    private  String emailId;

    @JsonProperty
    private  String assignedBy;

    @JsonProperty
    private  String labID;

    @JsonProperty
    private  String accessionId;

    @JsonProperty
    private  String caseAcct;

    @JsonProperty
    private  String hospital;

    @JsonProperty
    private  String caseStatus;

    @JsonProperty
    private  String orderControl;

    @JsonProperty
    private  String assignedDate;

    @JsonProperty
    private  String isScanned;

    @JsonProperty
    private  String messageType;

    @JsonProperty
    private  String operationType;

    @JsonProperty
    private  String dateReported;

    @JsonProperty
    private  String submissionId;
}