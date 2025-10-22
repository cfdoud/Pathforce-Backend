package com.pathdx.dto.requestDto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DesignationReqDto {

  @JsonProperty
  private Long id;

  @JsonProperty
  private String name;

}
