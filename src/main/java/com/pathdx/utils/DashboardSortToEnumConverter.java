package com.pathdx.utils;

import org.springframework.core.convert.converter.Converter;

public class DashboardSortToEnumConverter implements Converter<String, DashboardSort> {

    @Override
    public DashboardSort convert(String source) {
        try {
            return DashboardSort.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DashboardSort.CREATEDDATE;
        }
    }
}
