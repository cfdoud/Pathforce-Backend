package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseCommentsDto
{
    List<String> firstDiagnosis;
    List<String> caseSummary;
    List<String> clinicalHistory;
    List<String> finalDiagnosis;
}