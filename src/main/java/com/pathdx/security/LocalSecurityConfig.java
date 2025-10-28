package com.pathdx.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Profile("local")
@Configuration
public class LocalSecurityConfig {
  @Bean
  public UserDetailsService userDetailsService() {
    return new InMemoryUserDetailsManager(
      User.withUsername("chip").password("{noop}demo123").roles("USER").build()
    );
  }
  @Bean
  public SecurityFilterChain filter(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeHttpRequests(a -> a.anyRequest().permitAll())  // open for demo
        .httpBasic();
    return http.build();
  }
}
