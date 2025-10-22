package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientsDto {
    String PatientName;
    String Dob;
    String gender;
    String ethnicity;
    String caseAccnt;
    String MRN;
}
