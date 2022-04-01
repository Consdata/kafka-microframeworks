package com.consdata.kafka.microframeworks.javalin.transaction;

import com.consdata.kafka.microframeworks.javalin.order.JsonDeserializer;
import com.consdata.kafka.microframeworks.javalin.order.JsonSerializer;
import com.consdata.kafka.microframeworks.javalin.order.Order;
import com.consdata.kafka.microframeworks.javalin.wallet.StockWallet;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.util.Properties;

import static com.consdata.kafka.microframeworks.javalin.order.OrderProducer.BUY_ORDER_TOPIC;
import static com.consdata.kafka.microframeworks.javalin.order.OrderProducer.SELL_ORDER_TOPIC;
import static com.consdata.kafka.microframeworks.javalin.transaction.TransactionConsumer.TRANSACTIONS_TOPIC;
import static org.apache.kafka.common.serialization.Serdes.String;
import static org.apache.kafka.common.serialization.Serdes.serdeFrom;

public class TransactionStream {

    private final StockWallet stockWallet;

    public TransactionStream(StockWallet stockWallet) {
        this.stockWallet = stockWallet;
    }

    public void startStream() {
        Serde<Transaction> transactionSerde = serdeFrom(
                new JsonSerializer<>(), new JsonDeserializer<>(Transaction.class));
        Serde<Order> orderSerde = serdeFrom(
                new JsonSerializer<>(), new JsonDeserializer<>(Order.class));

        Properties props = getProperties(orderSerde);

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Order> sellOrderStream = builder.stream(SELL_ORDER_TOPIC, Consumed.with(String(), orderSerde));
        KStream<String, Order> buyOrderStream = builder.stream(BUY_ORDER_TOPIC, Consumed.with(String(), orderSerde));

        sellOrderStream
                .join(buyOrderStream,
                        stockWallet::process,
                        JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMillis(100)),
                        StreamJoined.with(String(), orderSerde, orderSerde))
                .filter((key, transaction) -> transaction.getExecutionTimestamp() != null)
                .to(TRANSACTIONS_TOPIC, Produced.with(String(), transactionSerde));

        final Topology topology = builder.build();
        KafkaStreams transactionStream = new KafkaStreams(topology, props);
        transactionStream.start();
    }

    private Properties getProperties(Serde<Order> orderSerde) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "javalin-transaction-stream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, orderSerde.getClass());
        return props;
    }

}
