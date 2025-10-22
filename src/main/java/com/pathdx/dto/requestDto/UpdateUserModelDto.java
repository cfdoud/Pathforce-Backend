package com.pathdx.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UpdateUserModelDto extends UserModelReqDto implements Serializable {


    @JsonProperty
    private Long id;



}
