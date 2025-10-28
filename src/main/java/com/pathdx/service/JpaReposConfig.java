package com.pathdx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Profile("!demo")
@Configuration
@EnableJpaRepositories(basePackages = "com.pathdx.repository")
public class JpaReposConfig { }
