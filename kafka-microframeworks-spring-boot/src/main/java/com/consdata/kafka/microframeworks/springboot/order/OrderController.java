package com.consdata.kafka.microframeworks.springboot.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{count}")
    public void generate(@PathVariable int count) {
        log.info(">> Generating {} buy and sell orders", count);
        long startTime = System.nanoTime();
        orderService.generate(count);
        log.info("<< Generated {} buy and sell orders in {}ms", count, (System.nanoTime() - startTime) / 1000000);
    }
}
