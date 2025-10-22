package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pathdx.dto.requestDto.CaseDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseListingDto {

    Long count;
    Map<String, Set<CaseDetailsDto>> accessionList;
}
