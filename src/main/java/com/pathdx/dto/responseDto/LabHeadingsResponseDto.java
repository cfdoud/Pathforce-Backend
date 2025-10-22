package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabHeadingsResponseDto {
    private String id;
    private String firstHeading;
    private String secondHeading;
    private String thirdHeading;
    private String fourthHeading;
    private String fifthHeading;
    private String sixthHeading;
    private String seventhHeading;
    private String labId;
}
