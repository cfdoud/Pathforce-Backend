package com.pathdx.config;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

@Profile("local")
@Configuration
public class CorsConfig implements WebMvcConfigurer {
  @Override public void addCorsMappings(CorsRegistry r) {
    r.addMapping("/**")
     .allowedOrigins("http://localhost:4200","http://localhost:8080")
     .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
     .allowedHeaders("*");
  }
}
