package com.pathdx.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StainsDto {
    private Long id;
    private Long slideId;
    private String name;
    private String abbr;
    private String stainType;
    private String quantity;
    private String cptCode;
    private String comments;
}
