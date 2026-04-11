package com.namsan.consumer.dao;

import com.namsan.consumer.model.Transaction;
import com.namsan.consumer.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Transaction entity
 * Provides low-level database operations
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionDAO {

    private final TransactionRepository transactionRepository;

    /**
     * Save a single transaction
     */
    public Transaction save(Transaction transaction) {
        log.debug("Saving transaction with account number: {}", transaction.getAccountNumber());
        return transactionRepository.save(transaction);
    }

    /**
     * Save multiple transactions
     */
    public List<Transaction> saveAll(List<Transaction> transactions) {
        log.debug("Saving {} transactions", transactions.size());
        return transactionRepository.saveAll(transactions);
    }

    /**
     * Find transaction by ID
     */
    public Optional<Transaction> findById(Long id) {
        log.debug("Finding transaction with ID: {}", id);
        return transactionRepository.findById(id);
    }

    /**
     * Find all transactions
     */
    public List<Transaction> findAll() {
        log.debug("Fetching all transactions");
        return transactionRepository.findAll();
    }

    /**
     * Find transactions by account number
     */
    public List<Transaction> findByAccountNumber(String accountNumber) {
        log.debug("Finding transactions for account number: {}", accountNumber);
        return transactionRepository.findByAccountNumber(accountNumber);
    }

    /**
     * Find transactions by account number and date
     */
    public List<Transaction> findByAccountNumberAndDate(String accountNumber, LocalDateTime transactionDate) {
        log.debug("Finding transactions for account: {} on date: {}", accountNumber, transactionDate);
        return transactionRepository.findByAccountNumberAndDate(accountNumber, transactionDate);
    }

    /**
     * Find transactions by source
     */
    public List<Transaction> findBySource(String source) {
        log.debug("Finding transactions from source: {}", source);
        return transactionRepository.findBySource(source);
    }

    /**
     * Find transactions by status
     */
    public List<Transaction> findByStatus(String status) {
        log.debug("Finding transactions with status: {}", status);
        return transactionRepository.findByStatus(status);
    }

    /**
     * Find transactions by account number and status
     */
    public List<Transaction> findByAccountNumberAndStatus(String accountNumber, String status) {
        log.debug("Finding transactions for account: {} with status: {}", accountNumber, status);
        return transactionRepository.findByAccountNumberAndStatus(accountNumber, status);
    }

    /**
     * Count transactions by account number
     */
    public long countByAccountNumber(String accountNumber) {
        log.debug("Counting transactions for account number: {}", accountNumber);
        return transactionRepository.countByAccountNumber(accountNumber);
    }

    /**
     * Count transactions by source
     */
    public long countBySource(String source) {
        log.debug("Counting transactions from source: {}", source);
        return transactionRepository.countBySource(source);
    }

    /**
     * Update a transaction
     */
    public Transaction update(Transaction transaction) {
        log.debug("Updating transaction with ID: {}", transaction.getId());
        return transactionRepository.save(transaction);
    }

    /**
     * Delete transaction by ID
     */
    public void delete(Long id) {
        log.debug("Deleting transaction with ID: {}", id);
        transactionRepository.deleteById(id);
    }

    /**
     * Delete all transactions
     */
    public void deleteAll() {
        log.debug("Deleting all transactions");
        transactionRepository.deleteAll();
    }

    /**
     * Get total transaction count
     */
    public long getTotalCount() {
        log.debug("Getting total transaction count");
        return transactionRepository.count();
    }
}

