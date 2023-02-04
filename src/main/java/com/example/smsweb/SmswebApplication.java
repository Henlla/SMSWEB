package com.example.smsweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SmswebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmswebApplication.class, args);
    }

}
