package com.consdata.kafka.microframeworks.javalin;

import com.consdata.kafka.microframeworks.javalin.order.OrderController;
import com.consdata.kafka.microframeworks.javalin.order.OrderProducer;
import com.consdata.kafka.microframeworks.javalin.order.OrderService;
import com.consdata.kafka.microframeworks.javalin.transaction.TransactionConsumer;
import com.consdata.kafka.microframeworks.javalin.transaction.TransactionStream;
import com.consdata.kafka.microframeworks.javalin.wallet.StockWallet;
import io.javalin.Javalin;

public class JavalinApplication {

    private static final String KAFKA_BOOTSTRAP_SERVER = "kafka:9092";

    public static void main(String[] args) {
        OrderController orderController = orderController();

        TransactionStream transactionStream = transactionStream();
        transactionStream.startStream();

        TransactionConsumer transactionConsumer = transactionConsumer();
        transactionConsumer.startConsuming();

        Javalin app = Javalin.create().start(8084);
        app.post("/order/<count>", ctx ->
                orderController.generate(ctx.pathParamAsClass("count", Integer.class).get()));
    }

    private static OrderController orderController() {
        OrderProducer orderProducer = new OrderProducer(KAFKA_BOOTSTRAP_SERVER);
        OrderService orderService = new OrderService(orderProducer);
        return new OrderController(orderService);
    }

    private static TransactionStream transactionStream() {
        StockWallet stockWallet = new StockWallet();
        stockWallet.initWalletWithRandomValues();
        return new TransactionStream(KAFKA_BOOTSTRAP_SERVER, stockWallet);
    }

    private static TransactionConsumer transactionConsumer() {
        return new TransactionConsumer(KAFKA_BOOTSTRAP_SERVER);
    }
}
