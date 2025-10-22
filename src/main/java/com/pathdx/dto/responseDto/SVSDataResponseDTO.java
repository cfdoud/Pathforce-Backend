package com.pathdx.dto.responseDto;

import lombok.Data;

import java.net.URL;
import java.util.List;
import java.util.Map;

@Data
public class SVSDataResponseDTO {
    private String height;
    private String width;
    private String mpp;
    Map<String, Map<String, List<URL>>> labelListMap;
    String bucketName;
    String tiledBucketName;
    String accessionId;
    String barcode;
}
