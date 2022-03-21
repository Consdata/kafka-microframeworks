package com.consdata.kafka.microframeworks.springboot.transaction;

import com.consdata.kafka.microframeworks.springboot.order.Order;
import com.consdata.kafka.microframeworks.springboot.wallet.CustomerWallet;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;
import java.util.function.BiConsumer;

@Slf4j
@Configuration
public class TransactionStream {

    private final CustomerWallet customerWallet;

    public static final String TRANSACTIONS_TOPIC = "spring-boot-transactions";

    public TransactionStream(CustomerWallet customerWallet) {
        this.customerWallet = customerWallet;
    }

    @Bean
    public BiConsumer<KStream<String, Order>, KStream<String, Order>> merge() {
        return (sellOrderStream, buyOrderStream) -> sellOrderStream
                .join(buyOrderStream,
                        this::process,
                        JoinWindows.of(Duration.ofMillis(100)),
                        StreamJoined.with(Serdes.String(), new JsonSerde<>(Order.class), new JsonSerde<>(Order.class)))
                .filter((key, transaction) -> transaction.getExecutionTimestamp() != null)
                .to(TRANSACTIONS_TOPIC, Produced.with(Serdes.String(), new JsonSerde<>(Transaction.class)));
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
