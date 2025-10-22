package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pathdx.model.SlideDetails;
import com.pathdx.model.SlideStainPanelModel;
import com.pathdx.model.SlideStainsModel;
import lombok.Data;

import java.util.List;

@Data
public class SlideDto {
    @JsonProperty
    private List<SlideDetails> slideDetails;

    @JsonProperty
    private List<SlideStainsModel> slideStainsModels;

    @JsonProperty
    private List<SlideStainPanelModel> slideStainPanelModels;
}
