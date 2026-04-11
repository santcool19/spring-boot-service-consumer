package com.namsan.consumer.dao;

import com.namsan.consumer.model.Transaction;
import com.namsan.consumer.repository.TransactionRepository;
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
 * Unit tests for TransactionDAO
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TransactionDAOTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionDAO transactionDAO;

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
    void testSave_Success() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionDAO.save(testTransaction);

        assertNotNull(result);
        assertEquals("ACC001", result.getAccountNumber());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testSaveAll_Success() {
        when(transactionRepository.saveAll(any(List.class))).thenReturn(testTransactions);

        List<Transaction> result = transactionDAO.saveAll(testTransactions);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    void testFindById_Success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        Optional<Transaction> result = transactionDAO.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("ACC001", result.get().getAccountNumber());
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Transaction> result = transactionDAO.findById(999L);

        assertFalse(result.isPresent());
        verify(transactionRepository, times(1)).findById(999L);
    }

    @Test
    void testFindAll_Success() {
        when(transactionRepository.findAll()).thenReturn(testTransactions);

        List<Transaction> result = transactionDAO.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testFindByAccountNumber_Success() {
        when(transactionRepository.findByAccountNumber("ACC001")).thenReturn(testTransactions);

        List<Transaction> result = transactionDAO.findByAccountNumber("ACC001");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ACC001", result.get(0).getAccountNumber());
        verify(transactionRepository, times(1)).findByAccountNumber("ACC001");
    }

    @Test
    void testFindByAccountNumberAndDate_Success() {
        LocalDateTime date = LocalDateTime.now();
        when(transactionRepository.findByAccountNumberAndDate("ACC001", date)).thenReturn(testTransactions);

        List<Transaction> result = transactionDAO.findByAccountNumberAndDate("ACC001", date);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findByAccountNumberAndDate("ACC001", date);
    }

    @Test
    void testFindBySource_Success() {
        when(transactionRepository.findBySource("REST")).thenReturn(testTransactions);

        List<Transaction> result = transactionDAO.findBySource("REST");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("REST", result.get(0).getSource());
        verify(transactionRepository, times(1)).findBySource("REST");
    }

    @Test
    void testFindByStatus_Success() {
        when(transactionRepository.findByStatus("PROCESSED")).thenReturn(testTransactions);

        List<Transaction> result = transactionDAO.findByStatus("PROCESSED");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PROCESSED", result.get(0).getStatus());
        verify(transactionRepository, times(1)).findByStatus("PROCESSED");
    }

    @Test
    void testCountByAccountNumber_Success() {
        when(transactionRepository.countByAccountNumber("ACC001")).thenReturn(5L);

        long result = transactionDAO.countByAccountNumber("ACC001");

        assertEquals(5L, result);
        verify(transactionRepository, times(1)).countByAccountNumber("ACC001");
    }

    @Test
    void testCountBySource_Success() {
        when(transactionRepository.countBySource("KAFKA")).thenReturn(10L);

        long result = transactionDAO.countBySource("KAFKA");

        assertEquals(10L, result);
        verify(transactionRepository, times(1)).countBySource("KAFKA");
    }

    @Test
    void testUpdate_Success() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionDAO.update(testTransaction);

        assertNotNull(result);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testDelete_Success() {
        transactionDAO.delete(1L);

        verify(transactionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAll_Success() {
        transactionDAO.deleteAll();

        verify(transactionRepository, times(1)).deleteAll();
    }

    @Test
    void testGetTotalCount_Success() {
        when(transactionRepository.count()).thenReturn(15L);

        long result = transactionDAO.getTotalCount();

        assertEquals(15L, result);
        verify(transactionRepository, times(1)).count();
    }
}

