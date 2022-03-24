package com.consdata.kafka.microframeworks.quarkus.order;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Slf4j
@Path("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @POST
    @Path("/{count}")
    public void hello(@PathParam int count) {
        log.info(">> Generating {} buy and sell orders", count);
        long startTime = System.nanoTime();
        orderService.generate(count);
        log.info("<< Generated {} buy and sell orders in {}ms", count, (System.nanoTime() - startTime) / 1000000);
    }
}
