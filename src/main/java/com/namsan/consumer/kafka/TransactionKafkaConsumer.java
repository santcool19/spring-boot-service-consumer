package com.namsan.consumer.kafka;

import com.namsan.consumer.model.Transaction;
import com.namsan.consumer.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka Consumer to consume transactions from Kafka topic
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionKafkaConsumer {

    private final TransactionService transactionService;

    /**
     * Listen to transactions from Kafka topic
     */
    @KafkaListener(
            topics = "transaction",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTransaction(Transaction transaction) {
        log.info("Received transaction from Kafka topic. Account: {}, Amount: {}",
                 transaction.getAccountNumber(), transaction.getTransactionAmount());

        try {
            // Set source as KAFKA
            if (transaction.getSource() == null) {
                transaction.setSource("KAFKA");
            }

            // Save transaction to database
            Transaction savedTransaction = transactionService.saveTransaction(transaction);
            log.info("Transaction saved successfully. ID: {}, Account: {}",
                     savedTransaction.getId(), savedTransaction.getAccountNumber());
        } catch (Exception e) {
            log.error("Error processing transaction from Kafka. Account: {}, Error: {}",
                      transaction.getAccountNumber(), e.getMessage(), e);
        }
    }

    /**
     * Listen to batch of transactions from Kafka topic
     */
    @KafkaListener(
            topics = "transaction",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory",
            id = "batch-listener"
    )
    public void consumeTransactionBatch(Transaction transaction) {
        log.debug("Processing transaction batch from Kafka. Account: {}", transaction.getAccountNumber());
        consumeTransaction(transaction);
    }
}

