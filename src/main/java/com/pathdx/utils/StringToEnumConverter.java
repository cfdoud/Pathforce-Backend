package com.pathdx.utils;

import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter implements Converter<String, CaseStatus> {

    @Override
    public CaseStatus convert(String source) {
        try {
            return CaseStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
