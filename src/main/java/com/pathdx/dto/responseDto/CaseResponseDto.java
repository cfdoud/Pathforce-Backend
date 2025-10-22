package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pathdx.model.CaseComments;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseResponseDto {
   String assignedUser;
   PatientsDto patient;
   CaseCommentsDto caseComments;
   OrderMessageDto orderMessage;
   Map<String,String> observations;
   String physicianName;
   String physicianPhone;
   URL reqPdfSingedUrl;
   boolean reportGeneratedFlag;

}
