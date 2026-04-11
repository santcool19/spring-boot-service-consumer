package com.namsan.consumer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactiondq")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @JsonProperty("account_number")
    private String accountNumber;

    @Column(nullable = false)
    @JsonProperty("transaction_amount")
    private BigDecimal transactionAmount;

    @Column(nullable = false)
    @JsonProperty("transaction_type")
    private String transactionType;

    @Column(nullable = false)
    @JsonProperty("transaction_date")
    private String transactionDate;

    @Column(nullable = false)
    @JsonProperty("description")
    private String description;

    @Column(nullable = false)
    @JsonProperty("status")
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "source", nullable = false)
    private String source; // "REST" or "KAFKA"

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "PROCESSED";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

