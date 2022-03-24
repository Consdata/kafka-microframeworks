package com.consdata.kafka.microframeworks.micronaut.order;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.MessageBody;

@KafkaClient
public interface OrderProducer {

    String SELL_ORDER_TOPIC = "micronaut-sell-orders";

    String BUY_ORDER_TOPIC = "micronaut-buy-orders";

    @Topic(SELL_ORDER_TOPIC)
    void produceSell(@KafkaKey String key, @MessageBody Order sellOrder);

    @Topic(BUY_ORDER_TOPIC)
    void produceBuy(@KafkaKey String key, @MessageBody Order sellOrder);
}
