package com.consdata.kafka.microframeworks.quarkus.transaction;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ApplicationScoped
public class TransactionConsumer {

    private final AtomicInteger transactionCounter = new AtomicInteger();

    @Incoming("transactions")
    public void consumeTransactions(List<Transaction> transactions) {
        if (!transactions.isEmpty()) {
            transactionCounter.addAndGet(transactions.size());
            log.info("Consumed {} transactions, {} overall so far", transactions.size(), transactionCounter.get());
        }
    }
}
