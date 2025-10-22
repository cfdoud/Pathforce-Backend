package com.pathdx.dto.adsync;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class AzureAdToken {

    @JsonProperty("token_type")
    String tokenType;

    @JsonProperty("expires_in")
    String expiresIn;

    @JsonProperty("ext_expires_in")
    String extExpiresIn;

    @JsonProperty("expires_on")
    String expiresOn;

    @JsonProperty("not_before")
    String notBefore;

    @JsonProperty("resource")
    String resource;

    @JsonProperty("access_token")
    String accessToken;
}
