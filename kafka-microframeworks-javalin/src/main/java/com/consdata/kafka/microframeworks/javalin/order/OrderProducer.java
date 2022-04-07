package com.consdata.kafka.microframeworks.javalin.order;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class OrderProducer {

    public static final String BUY_ORDER_TOPIC = "javalin-buy-orders";

    public static final String SELL_ORDER_TOPIC = "javalin-sell-orders";

    private static final String JAVALIN_ORDER_PRODUCER_ID = "javalin-order-producer";

    private final Producer<String, Order> producer;

    private final String bootstrapServer;

    public OrderProducer(String bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
        producer = createProducer();
    }

    private Producer<String, Order> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, JAVALIN_ORDER_PRODUCER_ID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public void produceSell(String key, Order order) {
        ProducerRecord<String, Order> orderRecord = new ProducerRecord<>(SELL_ORDER_TOPIC, key, order);
        producer.send(orderRecord);
    }

    public void produceBuy(String key, Order order) {
        ProducerRecord<String, Order> orderRecord = new ProducerRecord<>(BUY_ORDER_TOPIC, key, order);
        producer.send(orderRecord);
    }
}
