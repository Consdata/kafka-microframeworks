package com.consdata.kafka.microframeworks.springboot.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class TransactionConsumer {

    public static final String TRANSACTIONS_TOPIC = "spring-boot-transactions";

    private final AtomicInteger transactionCounter = new AtomicInteger();

    @KafkaListener(topics = TRANSACTIONS_TOPIC, groupId = "spring-boot-transactions")
    public void consumeTransactions(List<Transaction> transactions) {
        transactionCounter.addAndGet(transactions.size());
        log.info("Consumed {} transactions, {} overall so far", transactions.size(), transactionCounter.get());
    }
}
