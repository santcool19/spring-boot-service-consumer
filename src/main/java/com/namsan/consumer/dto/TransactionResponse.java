package com.namsan.consumer.dto;

import com.namsan.consumer.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for Transaction endpoints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private String message;
    private List<Transaction> data;
    private int statusCode;
    private String metadata;

    public TransactionResponse(String message, List<Transaction> data, int statusCode) {
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
        this.metadata = "";
    }
}

