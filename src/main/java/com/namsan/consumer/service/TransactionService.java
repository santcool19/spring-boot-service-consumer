package com.namsan.consumer.service;

import com.namsan.consumer.dao.TransactionDAO;
import com.namsan.consumer.model.Transaction;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Transaction processing
 * Implements business logic with resilience patterns
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionDAO transactionDAO;

    /**
     * Save a transaction with circuit breaker and retry pattern
     */
    @Transactional
    @CircuitBreaker(name = "transactionServiceCircuitBreaker", fallbackMethod = "saveTransactionFallback")
    @Retry(name = "transactionServiceRetry")
    @RateLimiter(name = "transactionServiceRateLimiter")
    public Transaction saveTransaction(Transaction transaction) {
        log.info("Saving transaction for account: {} from source: {}",
                 transaction.getAccountNumber(), transaction.getSource());
        return transactionDAO.save(transaction);
    }

    /**
     * Fallback method for saveTransaction
     */
    public Transaction saveTransactionFallback(Transaction transaction, Exception exception) {
        log.error("Circuit breaker activated for saveTransaction. Using fallback. Error: {}",
                  exception.getMessage());
        // Return transaction with error status for fallback
        transaction.setStatus("PENDING");
        return transaction;
    }

    /**
     * Save multiple transactions
     */
    @Transactional
    @CircuitBreaker(name = "transactionServiceCircuitBreaker", fallbackMethod = "saveTransactionsFallback")
    @Retry(name = "transactionServiceRetry")
    public List<Transaction> saveTransactions(List<Transaction> transactions) {
        log.info("Saving {} transactions", transactions.size());
        return transactionDAO.saveAll(transactions);
    }

    /**
     * Fallback method for saveTransactions
     */
    public List<Transaction> saveTransactionsFallback(List<Transaction> transactions, Exception exception) {
        log.error("Circuit breaker activated for saveTransactions. Using fallback. Error: {}",
                  exception.getMessage());
        // Mark all transactions as pending
        transactions.forEach(t -> t.setStatus("PENDING"));
        return transactions;
    }

    /**
     * Get transaction by ID with caching
     */
    @Cacheable(value = "transaction", key = "#id")
    @CircuitBreaker(name = "transactionServiceCircuitBreaker", fallbackMethod = "getTransactionFallback")
    public Optional<Transaction> getTransaction(Long id) {
        log.debug("Fetching transaction with ID: {}", id);
        return transactionDAO.findById(id);
    }

    /**
     * Fallback method for getTransaction
     */
    public Optional<Transaction> getTransactionFallback(Long id, Exception exception) {
        log.error("Circuit breaker activated for getTransaction. Error: {}", exception.getMessage());
        return Optional.empty();
    }

    /**
     * Get all transactions with caching
     */
    @Cacheable(value = "allTransactions")
    @CircuitBreaker(name = "transactionServiceCircuitBreaker", fallbackMethod = "getAllTransactionsFallback")
    public List<Transaction> getAllTransactions() {
        log.debug("Fetching all transactions");
        return transactionDAO.findAll();
    }

    /**
     * Fallback method for getAllTransactions
     */
    public List<Transaction> getAllTransactionsFallback(Exception exception) {
        log.error("Circuit breaker activated for getAllTransactions. Error: {}", exception.getMessage());
        return List.of();
    }

    /**
     * Get transactions by account number with caching and retry
     */
    @Cacheable(value = "transactionsByAccount", key = "#accountNumber")
    @Retry(name = "transactionServiceRetry")
    @CircuitBreaker(name = "transactionServiceCircuitBreaker", fallbackMethod = "getTransactionsByAccountFallback")
    @RateLimiter(name = "transactionServiceRateLimiter")
    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        log.debug("Fetching transactions for account: {}", accountNumber);
        return transactionDAO.findByAccountNumber(accountNumber);
    }

    /**
     * Fallback method for getTransactionsByAccount
     */
    public List<Transaction> getTransactionsByAccountFallback(String accountNumber, Exception exception) {
        log.error("Circuit breaker activated for getTransactionsByAccount. Error: {}", exception.getMessage());
        return List.of();
    }

    /**
     * Get transactions by account number and date with retry
     */
    @Retry(name = "transactionServiceRetry")
    @CircuitBreaker(name = "transactionServiceCircuitBreaker", fallbackMethod = "getTransactionsByAccountAndDateFallback")
    @RateLimiter(name = "transactionServiceRateLimiter")
    public List<Transaction> getTransactionsByAccountAndDate(String accountNumber, LocalDateTime date) {
        log.debug("Fetching transactions for account: {} on date: {}", accountNumber, date);
        return transactionDAO.findByAccountNumberAndDate(accountNumber, date);
    }

    /**
     * Fallback method for getTransactionsByAccountAndDate
     */
    public List<Transaction> getTransactionsByAccountAndDateFallback(String accountNumber, LocalDateTime date, Exception exception) {
        log.error("Circuit breaker activated for getTransactionsByAccountAndDate. Error: {}", exception.getMessage());
        return List.of();
    }

    /**
     * Get transactions by source with caching
     */
    @Cacheable(value = "transactionsBySource", key = "#source")
    public List<Transaction> getTransactionsBySource(String source) {
        log.debug("Fetching transactions from source: {}", source);
        return transactionDAO.findBySource(source);
    }

    /**
     * Get transactions by status with caching
     */
    @Cacheable(value = "transactionsByStatus", key = "#status")
    public List<Transaction> getTransactionsByStatus(String status) {
        log.debug("Fetching transactions with status: {}", status);
        return transactionDAO.findByStatus(status);
    }

    /**
     * Update transaction and invalidate cache
     */
    @Transactional
    @CacheEvict(value = "transaction", key = "#transaction.id", allEntries = false)
    @CircuitBreaker(name = "transactionServiceCircuitBreaker", fallbackMethod = "updateTransactionFallback")
    public Transaction updateTransaction(Transaction transaction) {
        log.info("Updating transaction with ID: {}", transaction.getId());
        return transactionDAO.update(transaction);
    }

    /**
     * Fallback method for updateTransaction
     */
    public Transaction updateTransactionFallback(Transaction transaction, Exception exception) {
        log.error("Circuit breaker activated for updateTransaction. Error: {}", exception.getMessage());
        return transaction;
    }

    /**
     * Get transaction count by account number with caching
     */
    @Cacheable(value = "transactionCountByAccount", key = "#accountNumber")
    public long getTransactionCount(String accountNumber) {
        log.debug("Getting transaction count for account: {}", accountNumber);
        return transactionDAO.countByAccountNumber(accountNumber);
    }

    /**
     * Get transaction count by source with caching
     */
    @Cacheable(value = "transactionCountBySource", key = "#source")
    public long getTransactionCountBySource(String source) {
        log.debug("Getting transaction count for source: {}", source);
        return transactionDAO.countBySource(source);
    }

    /**
     * Get total transaction count
     */
    @Cacheable(value = "totalTransactionCount")
    public long getTotalTransactionCount() {
        log.debug("Getting total transaction count");
        return transactionDAO.getTotalCount();
    }
}

