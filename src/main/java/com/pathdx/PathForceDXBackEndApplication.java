package com.pathdx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableJpaRepositories(basePackages = "com.pathdx.*")
@SpringBootApplication
@EnableScheduling
public class PathForceDXBackEndApplication {

    public static void main(String[] args) {

        SpringApplication.run(PathForceDXBackEndApplication.class, args);
    }


}

