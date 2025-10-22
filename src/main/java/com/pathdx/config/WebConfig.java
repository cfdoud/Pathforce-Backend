package com.pathdx.config;

import com.pathdx.utils.CaseListingStatusToEnumConverter;
import com.pathdx.utils.DashboardSortToEnumConverter;
import com.pathdx.utils.StringToEnumConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEnumConverter());
        registry.addConverter(new CaseListingStatusToEnumConverter());
        registry.addConverter(new DashboardSortToEnumConverter());
    }
}
