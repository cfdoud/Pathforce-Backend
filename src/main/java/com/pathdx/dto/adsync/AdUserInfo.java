package com.pathdx.dto.adsync;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class AdUserInfo {

    @JsonProperty("@odata.context")
    String context;

    @JsonProperty("@odata.nextLink")
    String nextLink;

    @JsonProperty("value")
    List<Value> value;
}
