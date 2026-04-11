package com.namsan.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Service Consumer Application - Main Entry Point
 *
 * This microservice consumes transactions from:
 * 1. REST Endpoint: http://localhost:8080/transactions/{Account_Number}/{Date}
 * 2. Kafka Topic: transaction
 *
 * Features:
 * - Circuit Breaker Pattern for fault tolerance
 * - Retry mechanism for resilience
 * - Rate Limiter for traffic control
 * - Caching for performance optimization
 * - Embedded H2 Database for storage
 */
@SpringBootApplication
public class ServiceConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceConsumerApplication.class, args);
    }
}

