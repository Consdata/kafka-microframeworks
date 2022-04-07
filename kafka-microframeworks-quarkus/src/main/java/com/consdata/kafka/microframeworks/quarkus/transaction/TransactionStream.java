package com.consdata.kafka.microframeworks.quarkus.transaction;

import com.consdata.kafka.microframeworks.quarkus.order.Order;
import com.consdata.kafka.microframeworks.quarkus.wallet.StockWallet;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.time.Duration;

import static org.apache.kafka.common.serialization.Serdes.String;

@Slf4j
@ApplicationScoped
public class TransactionStream {

    private final StockWallet stockWallet;

    public static final String TRANSACTIONS_TOPIC = "quarkus-transactions";

    public static final String SELL_ORDERS_TOPIC = "quarkus-sell-orders";

    public static final String BUY_ORDER_TOPIC = "quarkus-buy-orders";

    public TransactionStream(StockWallet stockWallet) {
        this.stockWallet = stockWallet;
    }

    @Produces
    public Topology transactionStream() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, Order> sellOrders = streamsBuilder
                .stream(SELL_ORDERS_TOPIC, Consumed.with(String(), new JsonbSerde<>(Order.class)));
        KStream<String, Order> buyOrders = streamsBuilder
                .stream(BUY_ORDER_TOPIC, Consumed.with(String(), new JsonbSerde<>(Order.class)));

        sellOrders
                .join(buyOrders,
                        stockWallet::process,
                        JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMillis(10)),
                        StreamJoined.with(
                                String(),
                                new JsonbSerde<>(Order.class),
                                new JsonbSerde<>(Order.class)))
                .filter((key, transaction) -> transaction.getExecutionTimestamp() != null)
                .to(TRANSACTIONS_TOPIC, Produced.with(String(), new JsonbSerde<>(Transaction.class)));

        return streamsBuilder.build();
    }

}
