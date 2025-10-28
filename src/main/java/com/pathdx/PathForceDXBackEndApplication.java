package com.pathdx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PathForceDXBackEndApplication {

    public static void main(String[] args) {

        SpringApplication.run(PathForceDXBackEndApplication.class, args);
    }


}

