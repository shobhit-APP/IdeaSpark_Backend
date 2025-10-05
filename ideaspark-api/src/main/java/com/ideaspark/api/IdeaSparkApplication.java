package com.ideaspark.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {"com.ideaspark"})
@EnableMongoRepositories(basePackages = {"com.ideaspark.api.repository"})
@EnableMongoAuditing
public class IdeaSparkApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdeaSparkApplication.class, args);
    }
}
