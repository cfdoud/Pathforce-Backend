package com.pathdx.service;

import com.pathdx.dto.responseDto.ResponseDto;

public interface GoogleMapAPIService {
    String fetchAddressDetailsFromGoogleMapSearch(String searchString);

}
