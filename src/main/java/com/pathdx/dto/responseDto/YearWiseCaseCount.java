package com.pathdx.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearWiseCaseCount {
    Integer year;
    Integer month;
    Integer count;
}
