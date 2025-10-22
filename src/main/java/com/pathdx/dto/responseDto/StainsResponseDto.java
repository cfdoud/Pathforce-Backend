package com.pathdx.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class StainsResponseDto {
    private List<StainsDto> stains;
    private List<StainsPanelDto> stainsPanels;
}
