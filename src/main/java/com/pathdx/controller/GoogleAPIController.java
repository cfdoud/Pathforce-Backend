package com.pathdx.controller;

import com.pathdx.dto.responseDto.CaseImageResponseDto;
import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.service.GoogleMapAPIService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Validated
@Tag(name = "PathDx Website Google Map API", description = "Endpoint to consume Google Map API")
@RequestMapping("/search")
public class GoogleAPIController {

    private final GoogleMapAPIService googleMapAPIService;

    public GoogleAPIController(@Autowired GoogleMapAPIService googleMapAPIService) {
        this.googleMapAPIService = googleMapAPIService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/address")
    public String fetchAddressDetailsFromGoogleMapSearch(@RequestParam("searchString")
                                                         String searchString) {
        return googleMapAPIService.fetchAddressDetailsFromGoogleMapSearch(searchString);
    }



}
