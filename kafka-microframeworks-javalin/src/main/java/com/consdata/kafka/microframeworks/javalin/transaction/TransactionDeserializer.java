package com.consdata.kafka.microframeworks.javalin.transaction;

import com.consdata.kafka.microframeworks.javalin.order.JsonDeserializer;

public class TransactionDeserializer extends JsonDeserializer<Transaction> {

    public TransactionDeserializer() {
        super(Transaction.class);
    }
}
