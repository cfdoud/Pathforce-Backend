package com.pathdx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile("!demo")
@EnableJpaRepositories(basePackages = "com.pathdx.repository")
public class JpaRepoConfig {}
