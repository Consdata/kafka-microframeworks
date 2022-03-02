package com.consdata.kafka.microframeworks.springboot.order;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderProducer {

    private final KafkaTemplate<String, Order> kafkaTemplate;

    private static final String SELL_ORDER_TOPIC = "spring-boot-sell-orders";

    private static final String BUY_ORDER_TOPIC = "spring-boot-buy-orders";

    public OrderProducer(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceSell(Order order) {
        kafkaTemplate.send(SELL_ORDER_TOPIC, order.getOrderId(), order);
    }

    public void produceBuy(Order order) {
        kafkaTemplate.send(BUY_ORDER_TOPIC, order.getOrderId(), order);
    }
}
