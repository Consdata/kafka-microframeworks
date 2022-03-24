package com.consdata.kafka.microframeworks.micronaut.order;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Post("/{count}")
    public void generate(@PathVariable int count) {
        log.info(">> Generating {} buy and sell orders", count);
        long startTime = System.nanoTime();
        orderService.generate(count);
        log.info("<< Generated {} buy and sell orders in {}ms", count, (System.nanoTime() - startTime) / 1000000);
    }
}
