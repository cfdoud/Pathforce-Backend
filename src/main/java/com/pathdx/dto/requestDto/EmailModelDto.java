package com.pathdx.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class EmailModelDto implements Serializable {
    String from;
    String to;
    String subject;
    String body;
    List<String> cc;
    List<String> bcc;
}
