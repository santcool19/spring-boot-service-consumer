package com.namsan.consumer.client;

import com.namsan.consumer.model.Transaction;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * REST Client to consume transactions from external endpoint
 * Implements resilience patterns for fault tolerance
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionRestClient {

    private final RestTemplate restTemplate;

    @Value("${transaction.rest.endpoint:http://localhost:8080/transactions}")
    private String transactionEndpoint;

    /**
     * Fetch transactions from REST endpoint with resilience patterns
     */
    @Cacheable(value = "restTransactions", key = "#accountNumber + '-' + #date")
    @CircuitBreaker(name = "transactionServiceCircuitBreaker", fallbackMethod = "fetchTransactionsFallback")
    @Retry(name = "transactionServiceRetry")
    @RateLimiter(name = "transactionServiceRateLimiter")
    public List<Transaction> fetchTransactions(String accountNumber, String date) {
        log.info("Fetching transactions from REST endpoint for account: {} and date: {}", accountNumber, date);

        try {
            String url = String.format("%s/%s/%s", transactionEndpoint, accountNumber, date);
            log.debug("Calling URL: {}", url);

            ResponseEntity<Transaction[]> response = restTemplate.getForEntity(url, Transaction[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Transaction> transactions = new ArrayList<>();
                for (Transaction transaction : response.getBody()) {
                    transaction.setSource("REST");
                    transactions.add(transaction);
                }
                log.info("Successfully fetched {} transactions from REST endpoint", transactions.size());
                return transactions;
            } else {
                log.warn("Unexpected response status: {}", response.getStatusCode());
                return new ArrayList<>();
            }
        } catch (RestClientException e) {
            log.error("Error fetching transactions from REST endpoint: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch transactions from REST endpoint", e);
        }
    }

    /**
     * Fallback method for fetchTransactions
     */
    public List<Transaction> fetchTransactionsFallback(String accountNumber, String date, Exception exception) {
        log.error("Circuit breaker activated for fetchTransactions. Account: {}, Date: {}, Error: {}",
                  accountNumber, date, exception.getMessage());
        return new ArrayList<>();
    }

    /**
     * Fetch single transaction from REST endpoint
     */
    @Retry(name = "transactionServiceRetry")
    @CircuitBreaker(name = "transactionServiceCircuitBreaker", fallbackMethod = "fetchTransactionFallback")
    public Transaction fetchTransaction(String accountNumber, String date, String transactionId) {
        log.debug("Fetching single transaction for account: {}, date: {}, transactionId: {}",
                  accountNumber, date, transactionId);

        try {
            String url = String.format("%s/%s/%s/%s", transactionEndpoint, accountNumber, date, transactionId);
            ResponseEntity<Transaction> response = restTemplate.getForEntity(url, Transaction.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                response.getBody().setSource("REST");
                return response.getBody();
            } else {
                log.warn("Transaction not found with status: {}", response.getStatusCode());
                return null;
            }
        } catch (RestClientException e) {
            log.error("Error fetching transaction from REST endpoint: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch transaction from REST endpoint", e);
        }
    }

    /**
     * Fallback method for fetchTransaction
     */
    public Transaction fetchTransactionFallback(String accountNumber, String date, String transactionId, Exception exception) {
        log.error("Circuit breaker activated for fetchTransaction. Error: {}", exception.getMessage());
        return null;
    }
}

