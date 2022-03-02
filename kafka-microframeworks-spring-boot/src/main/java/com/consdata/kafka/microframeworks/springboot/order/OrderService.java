package com.consdata.kafka.microframeworks.springboot.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Slf4j
@Component
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
