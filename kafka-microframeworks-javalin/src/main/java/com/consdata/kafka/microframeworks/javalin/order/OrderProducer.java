package com.consdata.kafka.microframeworks.javalin.order;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class OrderProducer {

    private final static String BUY_ORDER_TOPIC = "javalin-buy-orders";

    private final static String SELL_ORDER_TOPIC = "javalin-sell-orders";

    private final static String BOOTSTRAP_SERVERS = "localhost:9092";

    private final Producer<String, Order> orderProducer;

    public OrderProducer() {
        orderProducer = createProducer();
    }

    private static Producer<String, Order> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "javalin-application");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public void produceSell(String key, Order order) {
        ProducerRecord<String, Order> record = new ProducerRecord<>(SELL_ORDER_TOPIC, key, order);
        orderProducer.send(record);
    }

    public void produceBuy(String key, Order order) {
        ProducerRecord<String, Order> record = new ProducerRecord<>(BUY_ORDER_TOPIC, key, order);
        orderProducer.send(record);
    }
}
