package com.pathdx.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepoDto {
    Long orderMessageId;
    String accessionId;
    String hospitalName;
    String pFName;
    String pMName;
    String pLName;
    String phFName;
    String phMName;
    String phLName;
    Date assignedDate;
    Date dateAndTime;

    //boolean scanned;
}
