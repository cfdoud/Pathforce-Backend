// src/main/java/com/pathdx/config/LocalNoJpaScanConfig.java
package com.pathdx.config;

import org.springframework.context.annotation.*;
@Profile("demo")
@Configuration
@ComponentScan(
  basePackages = "com.pathdx",
  excludeFilters = {
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.pathdx\\.service\\.impl\\..*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.pathdx\\.repository\\..*")
  }
)
public class DemoNoJpaScanConfig {}
