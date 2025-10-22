package com.pathdx.service.impl;

import com.pathdx.dto.responseDto.ResponseDto;
import com.pathdx.exception.PathDxIOException;
import com.pathdx.exception.UriSyntaxException;
import com.pathdx.service.GoogleMapAPIService;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class GoogleMapAPIServiceImpl implements GoogleMapAPIService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleMapAPIServiceImpl.class);

    @Value("${google.map.api.path}")
    private String googleMapAPIPath;
    @Value("${google.map.api.host}")
    private String googleMapAPIHost;
    @Value("${google.map.api.scheme}")
    private String googleMapAPIScheme;
    @Value("${google.map.api.apiKey}")
    private String apiKey;

    private final HttpClient httpClient;

    public GoogleMapAPIServiceImpl(@Autowired HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String fetchAddressDetailsFromGoogleMapSearch(String searchString) {
        LOGGER.info("Fetching the address details from the Google Map search API {}.", searchString);
        String respFromBody = null;
        URIBuilder builder = createURI(searchString, apiKey);

        try {
            URI url = builder.build();
            HttpRequest request = HttpRequest.newBuilder(url).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            respFromBody = response.body();
        } catch (URISyntaxException uriSyntaxException) {
            String errorMessage = "An error occurred while fetching address from Google Map API due to bad URL.";
            LOGGER.error(errorMessage);
            throw new UriSyntaxException(errorMessage);
        } catch (InterruptedException interruptedException) {
            String errorMessage = "An error occurred while fetching address from Google Map API.";
            LOGGER.error(errorMessage);
            Thread.currentThread().interrupt();
        } catch (IOException ioException) {
            String errorMessage = "An error occurred while fetching address from Google Map API for given API Key.";
            LOGGER.error(errorMessage);
            throw new PathDxIOException(errorMessage);
        }
        return respFromBody;
    }



    private URIBuilder createURI(String searchString, String apiKey) {
        URIBuilder builder = new URIBuilder();
        builder.setScheme(googleMapAPIScheme);
        builder.setHost(googleMapAPIHost);
        builder.setPath(googleMapAPIPath);
        builder.addParameter("address", searchString);
        builder.addParameter("key", apiKey);
        return builder;
    }
}
