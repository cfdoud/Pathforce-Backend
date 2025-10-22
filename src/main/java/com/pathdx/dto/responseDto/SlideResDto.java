package com.pathdx.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SlideResDto {
    private List<SlideDetailsDto> slideDetailsDtos;
    private List<StainsDto> stainsDto;
    private List<StainsPanelDto> stainsPanelDto;
}
