package com.example.smsweb;

import com.example.smsweb.task.ScheduleTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableJpaAuditing
@EnableScheduling
public class SmswebApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmswebApplication.class, args);
    }
}
