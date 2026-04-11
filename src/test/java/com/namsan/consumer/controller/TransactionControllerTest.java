package com.namsan.consumer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namsan.consumer.model.Transaction;
import com.namsan.consumer.service.TransactionService;
import com.namsan.consumer.client.TransactionRestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for TransactionController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private TransactionRestClient transactionRestClient;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testGetAllTransactions_Success() throws Exception {
        when(transactionService.getAllTransactions()).thenReturn(testTransactions);

        mockMvc.perform(get("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("All transactions retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.statusCode", is(200)));

        verify(transactionService, times(1)).getAllTransactions();
    }

    @Test
    void testGetTransactionById_Success() throws Exception {
        when(transactionService.getTransaction(1L)).thenReturn(Optional.of(testTransaction));

        mockMvc.perform(get("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Transaction retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].account_number", is("ACC001")));

        verify(transactionService, times(1)).getTransaction(1L);
    }

    @Test
    void testGetTransactionById_NotFound() throws Exception {
        when(transactionService.getTransaction(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/transactions/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Transaction not found")))
                .andExpect(jsonPath("$.statusCode", is(404)));

        verify(transactionService, times(1)).getTransaction(999L);
    }

    @Test
    void testGetTransactionsByAccount_Success() throws Exception {
        when(transactionService.getTransactionsByAccount("ACC001")).thenReturn(testTransactions);

        mockMvc.perform(get("/api/transactions/account/ACC001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Transactions for account retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(transactionService, times(1)).getTransactionsByAccount("ACC001");
    }

    @Test
    void testGetTransactionsByAccountAndDate_Success() throws Exception {
        when(transactionService.getTransactionsByAccountAndDate("ACC001", LocalDateTime.now().toLocalDate().atStartOfDay()))
                .thenReturn(testTransactions);

        mockMvc.perform(get("/api/transactions/account/ACC001/date/2024-04-11")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    void testGetTransactionsBySource_Success() throws Exception {
        when(transactionService.getTransactionsBySource("REST")).thenReturn(testTransactions);

        mockMvc.perform(get("/api/transactions/source/REST")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Transactions from source retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(transactionService, times(1)).getTransactionsBySource("REST");
    }

    @Test
    void testGetStatistics_Success() throws Exception {
        when(transactionService.getTotalTransactionCount()).thenReturn(10L);
        when(transactionService.getTransactionCountBySource("KAFKA")).thenReturn(5L);
        when(transactionService.getTransactionCountBySource("REST")).thenReturn(5L);

        mockMvc.perform(get("/api/transactions/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Statistics retrieved successfully")))
                .andExpect(jsonPath("$.statusCode", is(200)));

        verify(transactionService, times(1)).getTotalTransactionCount();
        verify(transactionService, times(2)).getTransactionCountBySource(any());
    }

    @Test
    void testFetchFromRest_Success() throws Exception {
        when(transactionRestClient.fetchTransactions("ACC001", "2024-04-11")).thenReturn(testTransactions);
        when(transactionService.saveTransactions(any(List.class))).thenReturn(testTransactions);

        mockMvc.perform(get("/api/transactions/fetch-from-rest/ACC001/2024-04-11")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("successfully")))
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(transactionRestClient, times(1)).fetchTransactions("ACC001", "2024-04-11");
        verify(transactionService, times(1)).saveTransactions(any(List.class));
    }
}

