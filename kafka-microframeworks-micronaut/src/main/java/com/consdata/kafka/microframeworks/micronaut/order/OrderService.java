package com.consdata.kafka.microframeworks.micronaut.order;

import jakarta.inject.Singleton;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Singleton
public class OrderService {

    private final OrderProducer orderProducer;

    public OrderService(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    public void generate(int count) {
        CompletableFuture<Void> sellOrderProducer = CompletableFuture.runAsync(
                () -> Stream.generate(Order::sell).limit(count).forEach(order -> orderProducer.produceSell(order.getKey(), order)));
        CompletableFuture<Void> buyOrderProducer = CompletableFuture.runAsync(
                () -> Stream.generate(Order::buy).limit(count).forEach(order -> orderProducer.produceBuy(order.getKey(), order)));

        CompletableFuture.allOf(sellOrderProducer, buyOrderProducer).join();
    }
}
