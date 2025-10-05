package com.ideaspark.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for exception handling
 */
@Configuration
@ComponentScan(basePackages = "com.ideaspark.api.exception")
public class ExceptionHandlingConfig {
    // This configuration ensures that the GlobalExceptionHandler is loaded
}