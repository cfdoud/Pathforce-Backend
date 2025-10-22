package com.pathdx.dto.adsync;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class Value {

   @JsonProperty("businessPhones")
   List<String> businessPhones;

   @JsonProperty("displayName")
   String displayName;

   @JsonProperty("givenName")
   String givenName;

   @JsonProperty("jobTitle")
   String jobTitle;

   @JsonProperty("mail")
   String mail;

   @JsonProperty("mobilePhone")
   String mobilePhone;

   @JsonProperty("officeLocation")
   String officeLocation;

   @JsonProperty("preferredLanguage")
   String preferredLanguage;

   @JsonProperty("surname")
   String surname;

   @JsonProperty("userPrincipalName")
   String userPrincipalName;

   @JsonProperty("id")
   String id;

}