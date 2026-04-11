package com.namsan.consumer.integration;

import com.namsan.consumer.model.Transaction;
import com.namsan.consumer.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TransactionRepository using embedded H2 database
 */
@DataJpaTest
@ActiveProfiles("test")
public class TransactionRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testTransaction = Transaction.builder()
                .accountNumber("ACC001")
                .transactionAmount(new BigDecimal("1000.00"))
                .transactionType("DEBIT")
                .transactionDate("2024-04-11")
                .description("Integration Test Transaction")
                .status("PROCESSED")
                .source("REST")
                .build();
    }

    @Test
    void testSaveAndRetrieveTransaction() {
        Transaction savedTransaction = transactionRepository.save(testTransaction);
        entityManager.flush();

        assertNotNull(savedTransaction.getId());
        assertEquals("ACC001", savedTransaction.getAccountNumber());

        Transaction retrievedTransaction = transactionRepository.findById(savedTransaction.getId()).orElse(null);
        assertNotNull(retrievedTransaction);
        assertEquals("ACC001", retrievedTransaction.getAccountNumber());
    }

    @Test
    void testFindByAccountNumber() {
        transactionRepository.save(testTransaction);
        entityManager.flush();

        List<Transaction> transactions = transactionRepository.findByAccountNumber("ACC001");

        assertNotNull(transactions);
        assertTrue(transactions.size() > 0);
        assertTrue(transactions.stream().allMatch(t -> t.getAccountNumber().equals("ACC001")));
    }

    @Test
    void testFindBySource() {
        testTransaction.setSource("KAFKA");
        transactionRepository.save(testTransaction);
        entityManager.flush();

        List<Transaction> transactions = transactionRepository.findBySource("KAFKA");

        assertNotNull(transactions);
        assertTrue(transactions.size() > 0);
        assertTrue(transactions.stream().allMatch(t -> t.getSource().equals("KAFKA")));
    }

    @Test
    void testFindByStatus() {
        transactionRepository.save(testTransaction);
        entityManager.flush();

        List<Transaction> transactions = transactionRepository.findByStatus("PROCESSED");

        assertNotNull(transactions);
        assertTrue(transactions.size() > 0);
        assertTrue(transactions.stream().allMatch(t -> t.getStatus().equals("PROCESSED")));
    }

    @Test
    void testCountByAccountNumber() {
        for (int i = 0; i < 3; i++) {
            Transaction transaction = Transaction.builder()
                    .accountNumber("ACC001")
                    .transactionAmount(new BigDecimal("1000.00"))
                    .transactionType("DEBIT")
                    .transactionDate("2024-04-11")
                    .description("Test Transaction " + i)
                    .status("PROCESSED")
                    .source("REST")
                    .build();
            transactionRepository.save(transaction);
        }
        entityManager.flush();

        long count = transactionRepository.countByAccountNumber("ACC001");

        assertTrue(count >= 3);
    }

    @Test
    void testCountBySource() {
        for (int i = 0; i < 5; i++) {
            Transaction transaction = Transaction.builder()
                    .accountNumber("ACC001")
                    .transactionAmount(new BigDecimal("1000.00"))
                    .transactionType("DEBIT")
                    .transactionDate("2024-04-11")
                    .description("Test Transaction " + i)
                    .status("PROCESSED")
                    .source("REST")
                    .build();
            transactionRepository.save(transaction);
        }
        entityManager.flush();

        long count = transactionRepository.countBySource("REST");

        assertTrue(count >= 5);
    }

    @Test
    void testUpdateTransaction() {
        Transaction savedTransaction = transactionRepository.save(testTransaction);
        entityManager.flush();

        savedTransaction.setStatus("FAILED");
        Transaction updatedTransaction = transactionRepository.save(savedTransaction);
        entityManager.flush();

        assertEquals("FAILED", updatedTransaction.getStatus());
    }

    @Test
    void testDeleteTransaction() {
        Transaction savedTransaction = transactionRepository.save(testTransaction);
        entityManager.flush();

        Long savedId = savedTransaction.getId();
        transactionRepository.delete(savedTransaction);
        entityManager.flush();

        assertTrue(transactionRepository.findById(savedId).isEmpty());
    }

    @Test
    void testMultipleTransactionsPerAccount() {
        Transaction transaction1 = Transaction.builder()
                .accountNumber("ACC002")
                .transactionAmount(new BigDecimal("500.00"))
                .transactionType("CREDIT")
                .transactionDate("2024-04-11")
                .description("Transaction 1")
                .status("PROCESSED")
                .source("REST")
                .build();

        Transaction transaction2 = Transaction.builder()
                .accountNumber("ACC002")
                .transactionAmount(new BigDecimal("300.00"))
                .transactionType("DEBIT")
                .transactionDate("2024-04-11")
                .description("Transaction 2")
                .status("PROCESSED")
                .source("KAFKA")
                .build();

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        entityManager.flush();

        List<Transaction> transactions = transactionRepository.findByAccountNumber("ACC002");

        assertEquals(2, transactions.size());
        assertEquals(1, transactions.stream().filter(t -> t.getSource().equals("REST")).count());
        assertEquals(1, transactions.stream().filter(t -> t.getSource().equals("KAFKA")).count());
    }
}

