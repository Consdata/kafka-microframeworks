package com.consdata.kafka.microframeworks.javalin.order;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public void generate(int count) {
        log.info(">> Generating {} buy and sell orders", count);
        long startTime = System.nanoTime();
        orderService.generate(count);
        log.info("<< Generated {} buy and sell orders in {}ms", count, (System.nanoTime() - startTime) / 1000000);
    }
}
