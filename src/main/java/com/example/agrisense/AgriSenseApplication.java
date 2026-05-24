package com.example.agrisense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.example.agrisense.entity")
@EnableJpaRepositories("com.example.agrisense.repository")
public class AgriSenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgriSenseApplication.class, args);
    }

}
