package com.namsan.consumer.repository;

import com.namsan.consumer.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find transactions by account number
     */
    List<Transaction> findByAccountNumber(String accountNumber);

    /**
     * Find transactions by account number and date range
     */
    @Query("SELECT t FROM Transaction t WHERE t.accountNumber = :accountNumber " +
           "AND DATE(t.createdAt) = DATE(:transactionDate) ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountNumberAndDate(
            @Param("accountNumber") String accountNumber,
            @Param("transactionDate") LocalDateTime transactionDate);

    /**
     * Find transactions by source (REST or KAFKA)
     */
    List<Transaction> findBySource(String source);

    /**
     * Find transactions by status
     */
    List<Transaction> findByStatus(String status);

    /**
     * Find transactions by account number and status
     */
    List<Transaction> findByAccountNumberAndStatus(String accountNumber, String status);

    /**
     * Count transactions by account number
     */
    long countByAccountNumber(String accountNumber);

    /**
     * Count transactions by source
     */
    long countBySource(String source);
}

