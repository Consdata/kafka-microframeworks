package com.consdata.kafka.microframeworks.javalin;

import com.consdata.kafka.microframeworks.javalin.order.OrderController;
import com.consdata.kafka.microframeworks.javalin.order.OrderProducer;
import com.consdata.kafka.microframeworks.javalin.order.OrderService;
import io.javalin.Javalin;

public class JavalinApplication {

    public static void main(String[] args) {
        OrderController orderController = orderController();

        Javalin app = Javalin.create().start(8084);
        app.post("/order/<count>", ctx ->
                orderController.generate(ctx.pathParamAsClass("count", Integer.class).get()));
    }

    private static OrderController orderController() {
        OrderProducer orderProducer = new OrderProducer();
        OrderService orderService = new OrderService(orderProducer);
        return new OrderController(orderService);
    }
}
