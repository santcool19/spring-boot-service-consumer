package com.namsan.consumer.controller;

import com.namsan.consumer.client.TransactionRestClient;
import com.namsan.consumer.dto.TransactionResponse;
import com.namsan.consumer.model.Transaction;
import com.namsan.consumer.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * REST Controller for Transaction operations
 */
@Slf4j
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRestClient transactionRestClient;

    /**
     * Fetch transactions from external REST endpoint
     */
    @GetMapping("/fetch-from-rest/{accountNumber}/{date}")
    public ResponseEntity<?> fetchFromRest(
            @PathVariable String accountNumber,
            @PathVariable String date) {
        log.info("Fetch request from REST endpoint. Account: {}, Date: {}", accountNumber, date);

        try {
            List<Transaction> transactions = transactionRestClient.fetchTransactions(accountNumber, date);

            if (!transactions.isEmpty()) {
                // Save fetched transactions to database
                List<Transaction> savedTransactions = transactionService.saveTransactions(transactions);
                return ResponseEntity.ok(new TransactionResponse(
                        "Transactions fetched and saved successfully",
                        savedTransactions,
                        HttpStatus.OK.value()
                ));
            } else {
                return ResponseEntity.ok(new TransactionResponse(
                        "No transactions found",
                        List.of(),
                        HttpStatus.NOT_FOUND.value()
                ));
            }
        } catch (Exception e) {
            log.error("Error fetching transactions from REST: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TransactionResponse(
                            "Error fetching transactions: " + e.getMessage(),
                            List.of(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    ));
        }
    }

    /**
     * Get all transactions
     */
    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        log.info("Get all transactions request");

        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            return ResponseEntity.ok(new TransactionResponse(
                    "All transactions retrieved successfully",
                    transactions,
                    HttpStatus.OK.value()
            ));
        } catch (Exception e) {
            log.error("Error retrieving all transactions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TransactionResponse(
                            "Error retrieving transactions: " + e.getMessage(),
                            List.of(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    ));
        }
    }

    /**
     * Get transactions by account number
     */
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<?> getTransactionsByAccount(@PathVariable String accountNumber) {
        log.info("Get transactions for account: {}", accountNumber);

        try {
            List<Transaction> transactions = transactionService.getTransactionsByAccount(accountNumber);
            return ResponseEntity.ok(new TransactionResponse(
                    "Transactions for account retrieved successfully",
                    transactions,
                    HttpStatus.OK.value()
            ));
        } catch (Exception e) {
            log.error("Error retrieving transactions for account {}: {}", accountNumber, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TransactionResponse(
                            "Error retrieving transactions: " + e.getMessage(),
                            List.of(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    ));
        }
    }

    /**
     * Get transactions by account number and date
     */
    @GetMapping("/account/{accountNumber}/date/{date}")
    public ResponseEntity<?> getTransactionsByAccountAndDate(
            @PathVariable String accountNumber,
            @PathVariable String date) {
        log.info("Get transactions for account: {} on date: {}", accountNumber, date);

        try {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime dateTime = localDate.atStartOfDay();

            List<Transaction> transactions = transactionService.getTransactionsByAccountAndDate(accountNumber, dateTime);
            return ResponseEntity.ok(new TransactionResponse(
                    "Transactions for account and date retrieved successfully",
                    transactions,
                    HttpStatus.OK.value()
            ));
        } catch (Exception e) {
            log.error("Error retrieving transactions for account {} on date {}: {}",
                      accountNumber, date, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TransactionResponse(
                            "Error retrieving transactions: " + e.getMessage(),
                            List.of(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    ));
        }
    }

    /**
     * Get transaction by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        log.info("Get transaction with ID: {}", id);

        try {
            var transaction = transactionService.getTransaction(id);

            if (transaction.isPresent()) {
                return ResponseEntity.ok(new TransactionResponse(
                        "Transaction retrieved successfully",
                        List.of(transaction.get()),
                        HttpStatus.OK.value()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new TransactionResponse(
                                "Transaction not found",
                                List.of(),
                                HttpStatus.NOT_FOUND.value()
                        ));
            }
        } catch (Exception e) {
            log.error("Error retrieving transaction with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TransactionResponse(
                            "Error retrieving transaction: " + e.getMessage(),
                            List.of(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    ));
        }
    }

    /**
     * Get transactions by source
     */
    @GetMapping("/source/{source}")
    public ResponseEntity<?> getTransactionsBySource(@PathVariable String source) {
        log.info("Get transactions from source: {}", source);

        try {
            List<Transaction> transactions = transactionService.getTransactionsBySource(source);
            return ResponseEntity.ok(new TransactionResponse(
                    "Transactions from source retrieved successfully",
                    transactions,
                    HttpStatus.OK.value()
            ));
        } catch (Exception e) {
            log.error("Error retrieving transactions from source {}: {}", source, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TransactionResponse(
                            "Error retrieving transactions: " + e.getMessage(),
                            List.of(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    ));
        }
    }

    /**
     * Get statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStatistics() {
        log.info("Get transaction statistics");

        try {
            long totalCount = transactionService.getTotalTransactionCount();
            long kafkaCount = transactionService.getTransactionCountBySource("KAFKA");
            long restCount = transactionService.getTransactionCountBySource("REST");

            return ResponseEntity.ok(new TransactionResponse(
                    "Statistics retrieved successfully",
                    List.of(),
                    HttpStatus.OK.value(),
                    String.format("Total: %d, Kafka: %d, REST: %d", totalCount, kafkaCount, restCount)
            ));
        } catch (Exception e) {
            log.error("Error retrieving statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TransactionResponse(
                            "Error retrieving statistics: " + e.getMessage(),
                            List.of(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                    ));
        }
    }
}

