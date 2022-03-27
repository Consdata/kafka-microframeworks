package com.consdata.kafka.microframeworks.quarkus.transaction;

import com.consdata.kafka.microframeworks.quarkus.order.Order;
import com.consdata.kafka.microframeworks.quarkus.wallet.CustomerWallet;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.time.Duration;

@Slf4j
@ApplicationScoped
public class TransactionStream {

    private final CustomerWallet customerWallet;

    public static final String TRANSACTIONS_TOPIC = "quarkus-transactions";

    public TransactionStream(CustomerWallet customerWallet) {
        this.customerWallet = customerWallet;
    }

    @Produces
    public Topology transactionStream() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, Order> sellOrders = streamsBuilder.stream("quarkus-sell-orders", Consumed.with(Serdes.String(), new JsonbSerde<>(Order.class)));
        KStream<String, Order> buyOrders = streamsBuilder.stream("quarkus-buy-orders", Consumed.with(Serdes.String(), new JsonbSerde<>(Order.class)));

        sellOrders
                .join(buyOrders,
                        this::process,
                        JoinWindows.of(Duration.ofMillis(100)),
                        StreamJoined.with(Serdes.String(), new JsonbSerde<>(Order.class), new JsonbSerde<>(Order.class)))
                .filter((key, transaction) -> transaction.getExecutionTimestamp() != null)
                .to(TRANSACTIONS_TOPIC, Produced.with(Serdes.String(), new JsonbSerde<>(Transaction.class)));

        return streamsBuilder.build();
    }

    private Transaction process(Order sellOrder, Order buyOrder) {
        int sellPrice = sellOrder.getDesiredPricePerStock() * sellOrder.getAmount();
        int buyPrice = buyOrder.getDesiredPricePerStock() * buyOrder.getAmount();

        int sellerId = sellOrder.getCustomerId();
        int buyerId = buyOrder.getCustomerId();

        Transaction transaction = Transaction
                .builder()
                .sellingCustomerId(sellerId)
                .buyingCustomerId(buyerId)
                .stockSymbol(sellOrder.getStockSymbol())
                .amount(sellOrder.getAmount())
                .price(sellPrice)
                .build();

        if (buyPrice >= sellPrice && sellerId != buyerId) {
            return customerWallet.execute(transaction);
        }

        return transaction;
    }
}
