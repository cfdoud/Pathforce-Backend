package com.pathdx.utils;

import org.springframework.core.convert.converter.Converter;

public class CaseListingStatusToEnumConverter implements Converter<String, CaseListingStatus> {

    @Override
    public CaseListingStatus convert(String source) {
        try {
            return CaseListingStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
