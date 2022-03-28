package com.consdata.kafka.microframeworks.micronaut.transaction;

import com.consdata.kafka.microframeworks.micronaut.order.Order;
import com.consdata.kafka.microframeworks.micronaut.wallet.StockWallet;
import io.micronaut.configuration.kafka.serde.JsonObjectSerde;
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder;
import io.micronaut.context.annotation.Factory;
import io.micronaut.jackson.databind.JacksonDatabindMapper;
import io.micronaut.json.JsonObjectSerializer;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;

import static com.consdata.kafka.microframeworks.micronaut.order.OrderProducer.BUY_ORDER_TOPIC;
import static com.consdata.kafka.microframeworks.micronaut.order.OrderProducer.SELL_ORDER_TOPIC;
import static com.consdata.kafka.microframeworks.micronaut.transaction.TransactionConsumer.TRANSACTIONS_TOPIC;

@Factory
public class TransactionStream {

    private final StockWallet stockWallet;

    public TransactionStream(StockWallet stockWallet) {
        this.stockWallet = stockWallet;
    }

    @Singleton
    @Named("transaction-stream")
    public KStream<String, Order> joinOrders(ConfiguredStreamBuilder builder) {
        JsonObjectSerde<Order> orderSerde =
                new JsonObjectSerde<>(new JsonObjectSerializer(new JacksonDatabindMapper()), Order.class);
        JsonObjectSerde<Transaction> transactionSerde =
                new JsonObjectSerde<>(new JsonObjectSerializer(new JacksonDatabindMapper()), Transaction.class);

        KStream<String, Order> sellOrderStream = builder
                .stream(SELL_ORDER_TOPIC, Consumed.with(Serdes.String(), orderSerde));
        KStream<String, Order> buyOrderStream = builder
                .stream(BUY_ORDER_TOPIC, Consumed.with(Serdes.String(), orderSerde));

        sellOrderStream
                .join(buyOrderStream,
                        stockWallet::process,
                        JoinWindows.of(Duration.ofMillis(100)),
                        StreamJoined.with(Serdes.String(), orderSerde, orderSerde))
                .filter((key, transaction) -> transaction.getExecutionTimestamp() != null)
                .to(TRANSACTIONS_TOPIC, Produced.with(Serdes.String(), transactionSerde));

        return sellOrderStream;
    }

}
