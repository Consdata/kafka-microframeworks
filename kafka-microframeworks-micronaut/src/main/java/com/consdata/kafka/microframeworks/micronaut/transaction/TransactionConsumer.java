package com.consdata.kafka.microframeworks.micronaut.transaction;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@KafkaListener(groupId = "micronaut-transactions", batch = true)
public class TransactionConsumer {

    private final AtomicInteger transactionCounter = new AtomicInteger();

    public static final String TRANSACTIONS_TOPIC = "micronaut-transactions";

    @Topic(TRANSACTIONS_TOPIC)
    public void consumeTransaction(List<Transaction> transactions) {
        if (!transactions.isEmpty()) {
            transactionCounter.addAndGet(transactions.size());
            log.info("Consumed {} transactions, {} overall so far", transactions.size(), transactionCounter.get());
        }
    }
}
