package com.namsan.consumer.service;

import com.namsan.consumer.dao.TransactionDAO;
import com.namsan.consumer.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionService
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TransactionServiceTest {

    @Mock
    private TransactionDAO transactionDAO;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction testTransaction;
    private List<Transaction> testTransactions;

    @BeforeEach
    void setUp() {
        testTransaction = Transaction.builder()
                .id(1L)
                .accountNumber("ACC001")
                .transactionAmount(new BigDecimal("1000.00"))
                .transactionType("DEBIT")
                .transactionDate("2024-04-11")
                .description("Test Transaction")
                .status("PROCESSED")
                .source("REST")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testTransactions = new ArrayList<>();
        testTransactions.add(testTransaction);
    }

    @Test
    void testSaveTransaction_Success() {
        when(transactionDAO.save(any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionService.saveTransaction(testTransaction);

        assertNotNull(result);
        assertEquals("ACC001", result.getAccountNumber());
        verify(transactionDAO, times(1)).save(any(Transaction.class));
    }

    @Test
    void testSaveTransactions_Success() {
        when(transactionDAO.saveAll(any(List.class))).thenReturn(testTransactions);

        List<Transaction> result = transactionService.saveTransactions(testTransactions);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ACC001", result.get(0).getAccountNumber());
        verify(transactionDAO, times(1)).saveAll(any(List.class));
    }

    @Test
    void testGetTransaction_Success() {
        when(transactionDAO.findById(1L)).thenReturn(Optional.of(testTransaction));

        Optional<Transaction> result = transactionService.getTransaction(1L);

        assertTrue(result.isPresent());
        assertEquals("ACC001", result.get().getAccountNumber());
        verify(transactionDAO, times(1)).findById(1L);
    }

    @Test
    void testGetTransaction_NotFound() {
        when(transactionDAO.findById(999L)).thenReturn(Optional.empty());

        Optional<Transaction> result = transactionService.getTransaction(999L);

        assertFalse(result.isPresent());
        verify(transactionDAO, times(1)).findById(999L);
    }

    @Test
    void testGetAllTransactions_Success() {
        when(transactionDAO.findAll()).thenReturn(testTransactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionDAO, times(1)).findAll();
    }

    @Test
    void testGetTransactionsByAccount_Success() {
        when(transactionDAO.findByAccountNumber("ACC001")).thenReturn(testTransactions);

        List<Transaction> result = transactionService.getTransactionsByAccount("ACC001");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ACC001", result.get(0).getAccountNumber());
        verify(transactionDAO, times(1)).findByAccountNumber("ACC001");
    }

    @Test
    void testGetTransactionsByAccountAndDate_Success() {
        LocalDateTime date = LocalDateTime.now();
        when(transactionDAO.findByAccountNumberAndDate("ACC001", date)).thenReturn(testTransactions);

        List<Transaction> result = transactionService.getTransactionsByAccountAndDate("ACC001", date);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionDAO, times(1)).findByAccountNumberAndDate("ACC001", date);
    }

    @Test
    void testGetTransactionsBySource_Success() {
        when(transactionDAO.findBySource("REST")).thenReturn(testTransactions);

        List<Transaction> result = transactionService.getTransactionsBySource("REST");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("REST", result.get(0).getSource());
        verify(transactionDAO, times(1)).findBySource("REST");
    }

    @Test
    void testGetTransactionsByStatus_Success() {
        when(transactionDAO.findByStatus("PROCESSED")).thenReturn(testTransactions);

        List<Transaction> result = transactionService.getTransactionsByStatus("PROCESSED");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PROCESSED", result.get(0).getStatus());
        verify(transactionDAO, times(1)).findByStatus("PROCESSED");
    }

    @Test
    void testUpdateTransaction_Success() {
        testTransaction.setStatus("FAILED");
        when(transactionDAO.update(any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionService.updateTransaction(testTransaction);

        assertNotNull(result);
        assertEquals("FAILED", result.getStatus());
        verify(transactionDAO, times(1)).update(any(Transaction.class));
    }

    @Test
    void testGetTransactionCount_Success() {
        when(transactionDAO.countByAccountNumber("ACC001")).thenReturn(5L);

        long result = transactionService.getTransactionCount("ACC001");

        assertEquals(5L, result);
        verify(transactionDAO, times(1)).countByAccountNumber("ACC001");
    }

    @Test
    void testGetTransactionCountBySource_Success() {
        when(transactionDAO.countBySource("KAFKA")).thenReturn(10L);

        long result = transactionService.getTransactionCountBySource("KAFKA");

        assertEquals(10L, result);
        verify(transactionDAO, times(1)).countBySource("KAFKA");
    }

    @Test
    void testGetTotalTransactionCount_Success() {
        when(transactionDAO.getTotalCount()).thenReturn(15L);

        long result = transactionService.getTotalTransactionCount();

        assertEquals(15L, result);
        verify(transactionDAO, times(1)).getTotalCount();
    }
}

