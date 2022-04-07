package com.consdata.kafka.microframeworks.quarkus.order;

import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OrderProducer {

    @Inject
    @Channel("sell-order")
    @OnOverflow(value = OnOverflow.Strategy.UNBOUNDED_BUFFER)
    Emitter<Record<String, Order>> sellOrderEmitter;

    @Inject
    @Channel("buy-order")
    @OnOverflow(value = OnOverflow.Strategy.UNBOUNDED_BUFFER)
    Emitter<Record<String, Order>> buyOrderEmitter;

    public void produceSell(Order order) {
        sellOrderEmitter.send(Record.of(order.getKey(), order));
    }

    public void produceBuy(Order order) {
        buyOrderEmitter.send(Record.of(order.getKey(), order));
    }
}
