package com.mapuescuela;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MapuescuelaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MapuescuelaBackendApplication.class, args);
    }
}
