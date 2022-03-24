package com.consdata.kafka.microframeworks.quarkus.order;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@ApplicationScoped
public class OrderService {

    private final OrderProducer orderProducer;

    public OrderService(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    public void generate(int count) {
        CompletableFuture<Void> sellOrderProducer = CompletableFuture.runAsync(
                () -> Stream.generate(Order::sell).limit(count).forEach(orderProducer::produceSell));
        CompletableFuture<Void> buyOrderProducer = CompletableFuture.runAsync(
                () -> Stream.generate(Order::buy).limit(count).forEach(orderProducer::produceBuy));

        CompletableFuture.allOf(sellOrderProducer, buyOrderProducer).join();
    }
}
