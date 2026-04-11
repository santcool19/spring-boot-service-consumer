package com.namsan.consumer.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Test configuration for unit tests
 */
@TestConfiguration
public class TestConfig {

    @Bean
    public RestTemplate testRestTemplate() {
        return new RestTemplate();
    }
}

