package com.pathdx.dto.requestDto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StateReqDto {

  @JsonProperty
  private Long id;

  @JsonProperty
  private String stateName;

}
