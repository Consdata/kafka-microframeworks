package com.consdata.kafka.microframeworks.springboot.transaction;

import com.consdata.kafka.microframeworks.springboot.order.Order;
import com.consdata.kafka.microframeworks.springboot.wallet.CustomerWallet;
import com.consdata.kafka.microframeworks.springboot.wallet.Stock;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static com.consdata.kafka.microframeworks.springboot.wallet.Stock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TransactionStreamTest {

    private TestInputTopic<String, Order> buyOrderTopic;

    private TestInputTopic<String, Order> sellOrderTopic;

    private TestOutputTopic<String, Transaction> transactionTopic;

    private CustomerWallet customerWallet;

    private static final int SELLING_CUSTOMER_ID = 1;

    private static final int BUYING_CUSTOMER_ID = 2;

    @BeforeEach
    public void setUp() {
        Properties streamsConfiguration = createStreamsConfig();
        customerWallet = new CustomerWallet();
        customerWallet.initTestWallet();
        createTopology(streamsConfiguration);
    }

    @Test
    public void shouldCreateTransaction() {
        // when
        Stream.of(
                Order.testSell(SELLING_CUSTOMER_ID, MICROSOFT.getSymbol()),
                Order.testSell(SELLING_CUSTOMER_ID, GOOGLE.getSymbol()),
                Order.testSell(SELLING_CUSTOMER_ID, TESLA.getSymbol())
        ).forEach(order -> sellOrderTopic.pipeInput(order.getKey(), order));

        Stream.of(
                Order.testBuy(BUYING_CUSTOMER_ID, MICROSOFT.getSymbol()),
                Order.testBuy(BUYING_CUSTOMER_ID, GOOGLE.getSymbol()),
                Order.testBuy(BUYING_CUSTOMER_ID, TESLA.getSymbol())
        ).forEach(order -> buyOrderTopic.pipeInput(order.getKey(), order));

        // then
        assertThat(transactionTopic.isEmpty()).isFalse();

        List<Transaction> transactions = transactionTopic.readValuesToList();
        assertThat(transactions).isNotNull().hasSize(3);

        assertThat(transactions.get(0)).isNotNull().satisfies(transaction -> {
            assertThat(transaction.getStockSymbol()).isEqualTo(MICROSOFT.getSymbol());
            assertThat(transaction.getSellingCustomerId()).isEqualTo(SELLING_CUSTOMER_ID);
            assertThat(transaction.getBuyingCustomerId()).isEqualTo(BUYING_CUSTOMER_ID);
            assertThat(transaction.getAmount()).isEqualTo(10);
            assertThat(transaction.getPrice()).isEqualTo(100);
        });
        assertThat(transactions.get(1)).isNotNull().satisfies(transaction -> {
            assertThat(transaction.getStockSymbol()).isEqualTo(GOOGLE.getSymbol());
            assertThat(transaction.getSellingCustomerId()).isEqualTo(SELLING_CUSTOMER_ID);
            assertThat(transaction.getBuyingCustomerId()).isEqualTo(BUYING_CUSTOMER_ID);
            assertThat(transaction.getAmount()).isEqualTo(10);
            assertThat(transaction.getPrice()).isEqualTo(100);
        });
        assertThat(transactions.get(2)).satisfies(transaction -> {
            assertThat(transaction.getStockSymbol()).isEqualTo(TESLA.getSymbol());
            assertThat(transaction.getSellingCustomerId()).isEqualTo(SELLING_CUSTOMER_ID);
            assertThat(transaction.getBuyingCustomerId()).isEqualTo(BUYING_CUSTOMER_ID);
            assertThat(transaction.getAmount()).isEqualTo(10);
            assertThat(transaction.getPrice()).isEqualTo(100);
        });

        assertThat(customerWallet.getCustomerWalletMap().get(SELLING_CUSTOMER_ID).getBalance().get()).isEqualTo(1_000_300);
        assertThat(customerWallet.getCustomerWalletMap().get(BUYING_CUSTOMER_ID).getBalance().get()).isEqualTo(999_700);
    }

    private void createTopology(Properties streamsConfiguration) {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, Order> sellOrderStream = streamsBuilder.stream("spring-boot-sell-orders-test");
        KStream<String, Order> buyOrderStream = streamsBuilder.stream("spring-boot-buy-orders-test");

        BiConsumer<KStream<String, Order>, KStream<String, Order>> subject = new TransactionStream(customerWallet).merge();
        subject.accept(sellOrderStream, buyOrderStream);

        TopologyTestDriver topologyTestDriver = new TopologyTestDriver(streamsBuilder.build(), streamsConfiguration);

        sellOrderTopic = topologyTestDriver.createInputTopic("spring-boot-sell-orders-test", new StringSerializer(), new JsonSerde<>(Order.class).serializer());
        buyOrderTopic = topologyTestDriver.createInputTopic("spring-boot-buy-orders-test", new StringSerializer(), new JsonSerde<>(Order.class).serializer());
        transactionTopic = topologyTestDriver.createOutputTopic("spring-boot-transactions", new StringDeserializer(), new JsonSerde<>(Transaction.class).deserializer());
    }

    private Properties createStreamsConfig() {
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-microframeworks");
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, new JsonSerde<>(Order.class).getClass());
        streamsConfiguration.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        streamsConfiguration.put(JsonDeserializer.TRUSTED_PACKAGES, "com.consdata.kafka.microframeworks.springboot.order");
        return streamsConfiguration;
    }
}
