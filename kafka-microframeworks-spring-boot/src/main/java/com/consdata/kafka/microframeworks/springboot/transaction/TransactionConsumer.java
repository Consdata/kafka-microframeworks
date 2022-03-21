package com.consdata.kafka.microframeworks.springboot.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.consdata.kafka.microframeworks.springboot.transaction.TransactionStream.TRANSACTIONS_TOPIC;

@Slf4j
@Service
public class TransactionConsumer {

    private final AtomicInteger transactionCounter = new AtomicInteger();

    @KafkaListener(topics = TRANSACTIONS_TOPIC, groupId = "spring-boot-transactions")
    public void consumeTransactions(List<Transaction> transactions) {
        if (!transactions.isEmpty()) {
            transactionCounter.addAndGet(transactions.size());
            log.info("Consumed {} transactions, {} overall so far", transactions.size(), transactionCounter.get());
        }
    }
}
