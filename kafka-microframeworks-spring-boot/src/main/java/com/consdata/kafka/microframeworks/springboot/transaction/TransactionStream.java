package com.consdata.kafka.microframeworks.springboot.transaction;

import com.consdata.kafka.microframeworks.springboot.order.Order;
import com.consdata.kafka.microframeworks.springboot.wallet.StockWallet;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;
import java.util.function.BiConsumer;

import static org.apache.kafka.common.serialization.Serdes.String;

@Slf4j
@Configuration
public class TransactionStream {

    private final StockWallet stockWallet;

    public static final String TRANSACTIONS_TOPIC = "spring-boot-transactions";

    public TransactionStream(StockWallet stockWallet) {
        this.stockWallet = stockWallet;
    }

    @Bean
    public BiConsumer<KStream<String, Order>, KStream<String, Order>> merge() {
        return (sellOrderStream, buyOrderStream) -> sellOrderStream
                .join(buyOrderStream,
                        stockWallet::process,
                        JoinWindows.of(Duration.ofMillis(100)),
                        StreamJoined.with(
                                String(),
                                new JsonSerde<>(Order.class),
                                new JsonSerde<>(Order.class)))
                .filter((key, transaction) -> transaction.getExecutionTimestamp() != null)
                .to(TRANSACTIONS_TOPIC, Produced.with(String(), new JsonSerde<>(Transaction.class)));
    }

}
