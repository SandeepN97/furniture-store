package com.samjhana.ventures;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VenturesDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(VenturesDashboardApplication.class, args);
    }
}
