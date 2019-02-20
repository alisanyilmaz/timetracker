package com.ayilmaz.timetracker.timetrackerbff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TimetrackerBffApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimetrackerBffApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

