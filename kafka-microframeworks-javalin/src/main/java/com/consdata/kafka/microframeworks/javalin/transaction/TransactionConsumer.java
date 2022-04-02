package com.consdata.kafka.microframeworks.javalin.transaction;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TransactionConsumer {

    private final AtomicInteger transactionCounter = new AtomicInteger();

    private boolean consume = true;

    public static final String TRANSACTIONS_TOPIC = "javalin-transactions";

    public void startConsuming() {
        CompletableFuture.runAsync(this::consumerThread);
    }

    private void consumerThread() {
        Properties consumerProperties = createProperties();

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties)) {
            consumer.subscribe(Collections.singletonList(TRANSACTIONS_TOPIC));
            while (consume) {
                ConsumerRecords<String, String> transactions = consumer.poll(Duration.ofMillis(100));
                if (!transactions.isEmpty()) {
                    transactionCounter.addAndGet(transactions.count());
                    log.info("Consumed {} transactions, {} overall so far", transactions.count(), transactionCounter.get());
                }
            }
        }
    }

    @NotNull
    private Properties createProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "javalin-transaction-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        return props;
    }

    public void stopConsuming() {
        consume = false;
    }
}
